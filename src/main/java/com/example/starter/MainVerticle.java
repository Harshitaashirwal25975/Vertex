
package com.example.starter;

import io.ebean.Database;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.vertx.core.json.DecodeException;
import io.vertx.ext.web.handler.BodyHandler;


public class MainVerticle extends AbstractVerticle {

  private static final Logger logger = LoggerFactory.getLogger(MainVerticle.class);

  private void createUser(RoutingContext routingContext, Database database) {
    logger.info("Handling createUser request");

    // Log request headers and body for debugging
    logger.info("Request headers: {}", routingContext.request().headers());
    logger.info("Request body as string: {}", routingContext.getBodyAsString());

    try {
      JsonObject json = routingContext.getBodyAsJson();
      if (json == null) {
        throw new IllegalArgumentException("Request body is empty or not in JSON format");
      }

      logger.info("Request body as JSON: {}", json);

      String name = json.getString("name");
      String email = json.getString("email");
      String password = json.getString("password");

      if (name == null || email == null || password == null) {
        throw new IllegalArgumentException("Missing required fields: name and/or email");
      }
      String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

      User user = new User();
      user.setName(name);
      user.setEmail(email);
      user.setPassword(hashedPassword);


      database.save(user);

      routingContext.response()
        .setStatusCode(201)
        .end("User created successfully");
    } catch (DecodeException e) {
      // Handle case where the request body is not valid JSON
      logger.error("Invalid JSON: {}", e.getMessage());
      routingContext.response()
        .setStatusCode(400)
        .end("Invalid JSON format: " + e.getMessage());
    } catch (IllegalArgumentException e) {
      // Handle case where JSON is invalid or missing required fields
      logger.error("Invalid input: {}", e.getMessage());
      routingContext.response()
        .setStatusCode(400)
        .end("Invalid input: " + e.getMessage());
    } catch (Exception e) {
      // Handle other exceptions such as database errors
      logger.error("Internal server error: {}", e.getMessage(), e);
      routingContext.response()
        .setStatusCode(500)
        .end("Internal server error: " + e.getMessage());
    }
  }


  private void updateUser(RoutingContext routingContext, Database database) {
    logger.info("Handling updateUser request");

    // Log request headers and body for debugging
    logger.info("Request headers: {}", routingContext.request().headers());
    logger.info("Request body as string: {}", routingContext.getBodyAsString());

    try {
      JsonObject json = routingContext.getBodyAsJson();
      if (json == null) {
        throw new IllegalArgumentException("Request body is empty or not in JSON format");
      }

      logger.info("Request body as JSON: {}", json);

      Long id = json.getLong("id");
      String name = json.getString("name");
      String email = json.getString("email");

      if (id == null || name == null || email == null) {
        throw new IllegalArgumentException("Missing required fields: id, name, and/or email");
      }

      User user = database.find(User.class, id);
      if (user == null) {
        routingContext.response()
          .setStatusCode(404)
          .end("User not found");
        return;
      }

      user.setName(name);
      user.setEmail(email);

      database.update(user);

      routingContext.response()
        .setStatusCode(200)
        .end("User updated successfully");
    } catch (DecodeException e) {
      // Handle case where the request body is not valid JSON
      logger.error("Invalid JSON: {}", e.getMessage());
      routingContext.response()
        .setStatusCode(400)
        .end("Invalid JSON format: " + e.getMessage());
    } catch (IllegalArgumentException e) {
      // Handle case where JSON is invalid or missing required fields
      logger.error("Invalid input: {}", e.getMessage());
      routingContext.response()
        .setStatusCode(400)
        .end("Invalid input: " + e.getMessage());
    } catch (Exception e) {
      // Handle other exceptions such as database errors
      logger.error("Internal server error: {}", e.getMessage(), e);
      routingContext.response()
        .setStatusCode(500)
        .end("Internal server error: " + e.getMessage());
    }
  }
  private void loginUser(RoutingContext routingContext, Database database) {
    logger.info("Handling updateUser request");

    // Log request headers and body for debugging
    logger.info("Request headers: {}", routingContext.request().headers());
    logger.info("Request body as string: {}", routingContext.getBodyAsString());

    try {
      JsonObject json = routingContext.getBodyAsJson();
      if (json == null) {
        throw new IllegalArgumentException("Request body is empty or not in JSON format");
      }

      logger.info("Request body as JSON: {}", json);

      String password = json.getString("name");
      String email = json.getString("email");

      if (password == null || email == null) {
        throw new IllegalArgumentException("Missing required fields: password, email");
      }

      User user = database.find(User.class, email);
      if (user == null) {
        routingContext.response()
          .setStatusCode(404)
          .end("User not found");
        return;
      }

      if (user.getPassword() == password) {
        routingContext.response()
          .end("User logged in successfully");
      }
    }
    catch (Exception e) {
        logger.error("Internal server error: {}", e.getMessage(), e);
        routingContext.response()
          .setStatusCode(500)
          .end("Internal server error: " + e.getMessage());
      }
  }

  private void getUser(RoutingContext routingContext, Database database) {
    logger.info("Handling getUser request");

    try {
      String idParam = routingContext.request().getParam("id");
      if (idParam == null) {
        routingContext.response()
          .setStatusCode(400)
          .end("Missing required parameter: id");
        return;
      }

      Long id = Long.parseLong(idParam);
      User user = database.find(User.class, id);
      if (user == null) {
        routingContext.response()
          .setStatusCode(404)
          .end("User not found");
        return;
      }

      JsonObject userJson = new JsonObject()
        .put("id", user.getId())
        .put("name", user.getName())
        .put("email", user.getEmail());

      routingContext.response()
        .setStatusCode(200)
        .putHeader("Content-Type", "application/json")
        .end(userJson.encode());
    } catch (NumberFormatException e) {
      logger.error("Invalid id format: {}", e.getMessage());
      routingContext.response()
        .setStatusCode(400)
        .end("Invalid id format: " + e.getMessage());
    } catch (Exception e) {
      logger.error("Internal server error: {}", e.getMessage(), e);
      routingContext.response()
        .setStatusCode(500)
        .end("Internal server error: " + e.getMessage());
    }
  }


  private void deleteUser(RoutingContext routingContext, Database database) {
    logger.info("Handling deleteUser request");

    try {
      String userId = routingContext.pathParam("id");
      User user = database.find(User.class, Long.parseLong(userId));
      if (user == null) {
        routingContext.response()
          .setStatusCode(404)
          .end("User not found");
        return;
      }

      database.delete(user);

      routingContext.response()
        .setStatusCode(200)
        .end("User deleted successfully");
    } catch (Exception e) {
      logger.error("Error processing request: {}", e.getMessage(), e);
      routingContext.response()
        .setStatusCode(500)
        .end("Internal server error: " + e.getMessage());
    }
  }



  @Override
  public void start() {
    Database database = DBConfig.setup(); // Set up the database

    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create()); // Enable BodyHandler

    router.post("/create").handler(context -> createUser(context, database));
    router.put("/update").handler(context -> updateUser(context, database));
    router.get("/user/:id").handler(context -> getUser(context, database));
    router.delete("/user/:id").handler(context -> deleteUser(context, database));

    AuthHandler authHandler = new AuthHandler();
    router.post("/login").handler(context -> {
      System.out.println("hello");
      authHandler.handleLogin(context);
    });


    vertx.createHttpServer().requestHandler(router).listen(8888, http -> {
      if (http.succeeded()) {
        logger.info("HTTP server started on port 8888");
      } else {
        logger.error("HTTP server failed to start", http.cause());
      }
    });

    vertx.createHttpServer().requestHandler(router).listen(8888, http -> {
      if (http.succeeded()) {
        logger.info("HTTP server started on port 8888");
      } else {
        logger.error("HTTP server failed to start", http.cause());
      }
    });
  }
//  public static void main(String args[]){
//    Vertx var=Vertx.vertx();
//    var.deployVerticle(new MainVerticle());
//  }
}
