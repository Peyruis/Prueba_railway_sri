<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Perfiles de Cultivo - SRI</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Inter:opsz,wght@14..32,400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">
    <link rel="stylesheet" href="/css/dashboard.css">
    <link rel="stylesheet" href="/css/cultivos.css">
</head>
<body>
<jsp:include page="components/sidebar.jsp">
    <jsp:param name="activePage" value="cultivos" />
</jsp:include>

<div class="app-shell">
    <main class="main-area">
        <jsp:include page="components/navbar.jsp">
            <jsp:param name="title" value="Perfiles de cultivo" />
            <jsp:param name="subtitle" value="Configura umbrales de humedad y tratamiento recomendado por cultivo" />
        </jsp:include>

        <section class="content-panel">
            <div id="cultivoAlert" class="alert d-none fw-bold border-0 rounded-4 shadow-sm" role="alert"></div>

            <div class="d-flex flex-wrap align-items-center justify-content-between gap-3 mb-4">
                <button class="btn btn-success btn-new-profile" type="button" data-bs-toggle="modal" data-bs-target="#modalNuevoCultivo">
                    <i class="fa-solid fa-plus me-2"></i>
                    Nuevo Perfil
                </button>

                <div class="input-group search-box">
                    <input id="inputBuscarCultivo" type="search" class="form-control" placeholder="Buscar cultivo...">
                    <span class="input-group-text">
                        <i class="fa-solid fa-magnifying-glass"></i>
                    </span>
                </div>
            </div>

            <div id="cultivosGrid" class="row row-cols-1 row-cols-md-3 g-4">
                <c:forEach items="${cultivos}" var="cultivo">
                    <div class="col cultivo-item" data-id="${cultivo.id}" data-nombre="${cultivo.nombre}">
                        <article class="card cultivo-grid-card h-100 shadow-sm border-start border-success border-4">
                            <div class="card-body">
                                <h5 class="card-title cultivo-title">
                                    <i class="fa-solid fa-leaf text-success me-2"></i>
                                    ${cultivo.nombre}
                                </h5>

                                <div class="cultivo-detail">
                                    <i class="fa-solid fa-droplet text-success"></i>
                                    Humedad: ${cultivo.humedadMinOptima}% - ${cultivo.humedadMaxOptima}%
                                </div>
                                <div class="cultivo-detail">
                                    <i class="fa-solid fa-clock text-success"></i>
                                    Duraci&oacute;n de riego: ${cultivo.duracionRiegoMinutos} min
                                </div>
                                <p class="cultivo-notes">
                                    <c:choose>
                                        <c:when test="${empty cultivo.tratoRecomendado}">
                                            Sin tratamiento recomendado registrado.
                                        </c:when>
                                        <c:otherwise>
                                            ${cultivo.tratoRecomendado}
                                        </c:otherwise>
                                    </c:choose>
                                </p>
                            </div>

                            <div class="card-footer bg-white border-0 d-flex flex-wrap gap-2">
                                <button class="btn btn-sm btn-outline-success btn-editar" type="button" data-id="${cultivo.id}">
                                    <i class="fa-solid fa-pen me-1"></i>
                                    Editar
                                </button>
                                <button class="btn btn-sm btn-outline-danger btn-eliminar" type="button" data-id="${cultivo.id}">
                                    <i class="fa-solid fa-trash me-1"></i>
                                    Eliminar
                                </button>
                            </div>
                        </article>
                    </div>
                </c:forEach>
            </div>

            <c:if test="${empty cultivos}">
                <div id="emptyCultivos" class="empty-state mt-4">
                    <div>
                        <i class="fa-solid fa-seedling"></i>
                        <h4 class="fw-bold">Aún no hay perfiles</h4>
                        <p class="mb-0">Crea tu primer perfil desde el botón Nuevo Perfil.</p>
                    </div>
                </div>
            </c:if>
        </section>
    </main>
</div>

<div class="modal fade" id="modalNuevoCultivo" tabindex="-1" aria-labelledby="modalNuevoCultivoLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-lg">
        <div class="modal-content cultivo-modal">
            <form id="formNuevoCultivo">
                <div class="modal-header">
                    <h5 class="modal-title" id="modalNuevoCultivoLabel">
                        <i class="fa-solid fa-seedling text-success me-2"></i>
                        Nuevo perfil de cultivo
                    </h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>
                </div>

                <div class="modal-body">
                    <div id="cultivoModalAlert" class="alert alert-danger d-none fw-bold" role="alert"></div>

                    <div class="row g-3">
                        <div class="col-md-12">
                            <label class="form-label" for="nombreCultivo">Nombre del cultivo</label>
                            <input id="nombreCultivo" name="nombre" type="text" class="form-control" maxlength="50" required>
                        </div>

                        <div class="col-md-6">
                            <label class="form-label" for="humedadMinOptima">Humedad mín (%)</label>
                            <input id="humedadMinOptima" name="humedadMinOptima" type="number" min="0" max="100" class="form-control" required>
                        </div>

                        <div class="col-md-6">
                            <label class="form-label" for="humedadMaxOptima">Humedad máx (%)</label>
                            <input id="humedadMaxOptima" name="humedadMaxOptima" type="number" min="0" max="100" class="form-control" required>
                        </div>

                        <div class="col-md-6">
                            <label class="form-label" for="duracionRiegoMinutos">Duraci&oacute;n de riego (min)</label>
                            <input id="duracionRiegoMinutos" name="duracionRiegoMinutos" type="number" min="1" class="form-control" required>
                        </div>

                        <div class="col-md-12">
                            <label class="form-label" for="tratoRecomendado">Tratamiento recomendado</label>
                            <textarea id="tratoRecomendado" name="tratoRecomendado" class="form-control" rows="4"></textarea>
                        </div>
                    </div>
                </div>

                <div class="modal-footer">
                    <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Cancelar</button>
                        <button id="btnGuardarCultivo" type="submit" class="btn btn-success">
                            <i class="fa-solid fa-floppy-disk me-2"></i>
                            Guardar
                        </button>
                </div>
            </form>
        </div>
    </div>
</div>

<jsp:include page="components/scripts.jsp" />
<script src="/js/cultivos.js"></script>
</body>
</html>
