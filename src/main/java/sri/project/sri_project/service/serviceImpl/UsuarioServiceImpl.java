package sri.project.sri_project.service.serviceImpl;

import org.springframework.stereotype.Service;
import sri.project.sri_project.model.User;
import sri.project.sri_project.repository.UsuarioRepository;
import sri.project.sri_project.service.UsuarioService;
import sri.project.sri_project.service.EmailService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import java.util.Collections;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final EmailService emailService;

    public UsuarioServiceImpl(UsuarioRepository userRepository, EmailService emailService) {
        this.usuarioRepository = userRepository;
        this.emailService = emailService;
    }

    @Override
    public User ejecutar(String username, String passwordIngresada) {
        User user = usuarioRepository
                .findByEmailOrNombre(username, username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

        if (!passwordIngresada.equals(user.getPasswordHash())) {
            throw new IllegalArgumentException("Contraseña incorrecta.");
        }

        return user;
    }

    @Override
    public User autenticarConGoogle(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList("571504675373-b3dthv13b7i5khpi4p4dvnq09icbfe4n.apps.googleusercontent.com"))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                String email = payload.getEmail();
                String name = (String) payload.get("name");
                String pictureUrl = (String) payload.get("picture");

                User loggedInUser = usuarioRepository.findByEmail(email).map(user -> {
                    user.setPictureUrl(pictureUrl);
                    return usuarioRepository.save(user);
                }).orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setNombre(name);
                    newUser.setPasswordHash("");
                    newUser.setPictureUrl(pictureUrl);
                    return usuarioRepository.save(newUser);
                });
                
                // Enviar correo SIEMPRE al iniciar sesión (para que el usuario lo vea funcionar)
                new Thread(() -> {
                    try {
                        emailService.enviarCorreoBienvenida(email, name);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        try {
                            java.nio.file.Files.writeString(java.nio.file.Path.of("mail_error.log"), "Bienvenida Thread Error: " + ex.getMessage(), java.nio.file.StandardOpenOption.CREATE, java.nio.file.StandardOpenOption.APPEND);
                        } catch (Exception ignored) {}
                    }
                }).start();
                
                return loggedInUser;
            } else {
                throw new IllegalArgumentException("Token de Google inválido.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error verificando token de Google", e);
        }
    }
}
