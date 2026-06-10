<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<section class="chart-panel" id="chartPanel">
    <div class="chart-header">
        <div class="chart-title">
            <h3><i class="fa-solid fa-chart-line me-2"></i>Historial de humedad</h3>
            <span>Evolución reciente del sensor de suelo</span>
        </div>
        <div class="live-note">
            <span class="live-dot"></span>
            Actualización cada 3s
        </div>
    </div>
    <canvas id="humidityChart" class="chart-canvas"></canvas>
</section>
