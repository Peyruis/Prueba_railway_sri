<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<html>

<head>

    <meta charset="UTF-8">

    <title>Sensor ESP32</title>

    <meta http-equiv="refresh" content="2">

    <style>

        body{
            font-family: Arial;
            margin: 40px;
            background: #f5f5f5;
        }

        .card{
            width: 400px;
            padding: 25px;
            border-radius: 12px;
            background: white;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
        }

        .dato{
            font-size: 22px;
            margin-top: 20px;
        }

        h1{
            margin-bottom: 30px;
        }

    </style>

</head>

<body>

<div class="card">

    <h1>Datos ESP32</h1>

    <div class="dato">

        Humedad:
        <strong>${humedad} %</strong>

    </div>

    <div class="dato">

        Distancia:
        <strong>${distancia} cm</strong>

    </div>

</div>

</body>

</html>
