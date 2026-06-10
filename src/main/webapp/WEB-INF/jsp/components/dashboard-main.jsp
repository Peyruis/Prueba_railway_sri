<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<section class="content-panel">
    <div class="metric-grid">
        <jsp:include page="metric-card.jsp">
            <jsp:param name="icon" value="fa-solid fa-gauge-high" />
            <jsp:param name="label" value="Humedad del suelo" />
            <jsp:param name="valueClass" value="" />
            <jsp:param name="valueHtml" value='<span id="humidityValue">85</span>%' />
            <jsp:param name="sub" value="Húmedo" />
            <jsp:param name="subId" value="humidityLabel" />
        </jsp:include>

        <jsp:include page="metric-card.jsp">
            <jsp:param name="icon" value="fa-solid fa-circle-check" />
            <jsp:param name="label" value="Agua disponible" />
            <jsp:param name="valueClass" value="fs-4" />
            <jsp:param name="valueHtml" value='<i class="fa-solid fa-check-circle text-success"></i> Adecuado' />
            <jsp:param name="sub" value="Nivel suficiente" />
            <jsp:param name="subId" value="" />
        </jsp:include>

        <jsp:include page="metric-card.jsp">
            <jsp:param name="icon" value="fa-solid fa-pump-soap" />
            <jsp:param name="label" value="Bomba" />
            <jsp:param name="valueClass" value="fs-4" />
            <jsp:param name="valueHtml" value='<span class="state-pill">Encendida</span>' />
            <jsp:param name="sub" value="Funcionando riego activo" />
            <jsp:param name="subId" value="" />
        </jsp:include>

        <jsp:include page="metric-card.jsp">
            <jsp:param name="icon" value="fa-solid fa-robot" />
            <jsp:param name="label" value="Modo" />
            <jsp:param name="valueClass" value="fs-4" />
            <jsp:param name="valueHtml" value='<span class="state-pill manual"><i class="fa-solid fa-robot"></i> Manual</span>' />
            <jsp:param name="sub" value="Control manual" />
            <jsp:param name="subId" value="" />
        </jsp:include>
    </div>

    <jsp:include page="chart-panel.jsp" />
</section>
