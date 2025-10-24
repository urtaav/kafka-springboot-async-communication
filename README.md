# 🧾 Kafka Microservices Demo — Order, Payment, Shipping & Mail

Un ejemplo completo de **microservicios con Spring Boot** que utiliza **Apache Kafka** para la comunicación asíncrona entre servicios. El proyecto simula el flujo de un pedido, desde su creación hasta la notificación de envío por correo electrónico, incluyendo **notificaciones por correo electrónico HTML** a través de **Mailhog**.

## 🚀 Visión General

Este proyecto ejemplifica una **arquitectura real basada en eventos** donde cada servicio procesa una etapa del flujo de trabajo y notifica a la siguiente a través de un *topic* de Kafka.

**Flujo del Evento:**

$$
\text{OrderService} \longrightarrow \text{PaymentService} \longrightarrow \text{ShippingService} \longrightarrow \text{MailService}
$$

| Servicio | Puerto | Descripción |
| :--- | :--- | :--- |
| **Order Service** | `8081` | Produce eventos `order-placed` (simula la creación de un pedido). |
| **Payment Service** | `8082` | Consume eventos `order-placed`, produce eventos `payment-processed`. |
| **Shipping Service** | `8083` | Consume eventos `payment-processed`, produce eventos `shipping-confirmed`. |
| **Mail Service** | `8084` | Consume eventos `shipping-confirmed` y envía notificaciones por correo electrónico HTML vía **Mailhog**. |

---

## 🏗 Diagrama de Arquitectura

El siguiente diagrama ilustra la comunicación unidireccional y asíncrona entre los servicios a través de Kafka:

                ┌────────────────┐
                │ Order Service  │ (8081)
                │ Sends: order-placed
                └──────┬─────────┘
                       │
                       ▼
                ┌────────────────┐
                │ Payment Service│ (8082)
                │ Listens: order-placed
                │ Sends: payment-processed
                └──────┬─────────┘
                       │
                       ▼
                ┌────────────────┐
                │ Shipping Svc   │ (8083)
                │ Listens: payment-processed
                │ Sends: shipping-confirmed
                └──────┬─────────┘
                       │
                       ▼
                ┌────────────────┐
                │ Mail Service   │ (8084)
                │ Listens: shipping-confirmed
                │ Sends HTML email via Mailhog
                └────────────────┘
---

## 🛠 Requisitos

Asegúrate de tener instalados los siguientes componentes:

* **JDK 17+**
* **Maven 3.9+**
* **Docker Desktop** (Necesario para Kafka, Zookeeper y Mailhog)

---

## ⚙️ Instrucciones de Configuración

### 1️⃣ Iniciar Kafka, Zookeeper y Mailhog con Docker

Utiliza el archivo `docker-compose.yml` proporcionado para levantar la infraestructura de mensajes y correo electrónico.

```bash
docker-compose up -d
```


version: '3'
services:
  zookeeper:
    image: wurstmeister/zookeeper
    ports:
      - "2181:2181"

  kafka:
    image: wurstmeister/kafka
    ports:
      - "9092:9092"
    environment:
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1

  mailhog:
    image: mailhog/mailhog
    ports:
      - "1025:1025" # Puerto SMTP
      - "8025:8025" # Interfaz Web



# Order Service
cd order-service
mvn spring-boot:run

# Payment Service
cd payment-service
mvn spring-boot:run

# Shipping Service
cd shipping-service
mvn spring-boot:run

# Mail Service
cd mail-service
mvn spring-boot:run



Nombre del Topic,Productor,Consumidor,Descripción
order-placed,OrderService,PaymentService,Nuevo pedido creado
payment-processed,PaymentService,ShippingService,Pago confirmado
shipping-confirmed,ShippingService,MailService,Envío completado



Hello!

Your order #12345 has been shipped successfully. 🚚
Tracking Number: ABCD1234

Thank you for shopping with us!

– URTAAV Team
