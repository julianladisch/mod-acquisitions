package com.folio.rest.impl;

import java.io.IOException;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.config.EncoderConfig;
import com.jayway.restassured.response.Response;
import com.folio.rest.RestVerticle;
import com.folio.rest.persist.MongoCRUD;
import com.folio.rest.tools.utils.NetworkUtils;

import static com.jayway.restassured.http.ContentType.JSON;
import static com.jayway.restassured.http.ContentType.TEXT;
import static com.jayway.restassured.RestAssured.given;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.empty;

import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class FundsAPITest {
  private static Vertx vertx;
  
  /** funds path */
  private static String funds = "/apis/funds";
  /** invoices path */
  private static String invoices = "/apis/invoices";
  /** poline path */
  private static String poLines = "/apis/po_lines";

  private static void startEmbeddedMongo() throws Exception {
    MongoCRUD.setIsEmbedded(true);
    MongoCRUD.getInstance(vertx).startEmbeddedMongo();
  }

  private static void deployRestVerticle(TestContext context) {
    DeploymentOptions deploymentOptions = new DeploymentOptions().setConfig(
        new JsonObject().put("http.port", RestAssured.port));
    vertx.deployVerticle(RestVerticle.class.getName(), deploymentOptions,
            context.asyncAssertSuccess());
  }
  
  @Before
  public void setUp(TestContext context) {
    vertx = Vertx.vertx();
    RestAssured.port = NetworkUtils.nextFreePort(); 
    RestAssured.baseURI = "http://localhost";
    RestAssured.config = RestAssured.config().encoderConfig(EncoderConfig.encoderConfig()
        .appendDefaultContentCharsetToContentTypeIfUndefined(false));
    RestAssured.requestSpecification = new RequestSpecBuilder()
      .addHeader("Authorization", "authtoken")
      .build();

    try {
      startEmbeddedMongo();
    } catch (Exception e) {
      context.fail(e);
    }
    deployRestVerticle(context);
  }

  private String getFile(String filename) throws IOException {
    return IOUtils.toString(getClass().getClassLoader().getResourceAsStream(filename), "UTF-8");
  }
  
  @Test
  public void runTest() throws IOException {
    
    //get funds before inserting
    given().accept(TEXT).
    when().get(funds).
    then().
      body("total_records", equalTo(0)).
      body("funds", empty());
    
    //insert fund
    Response response =
    given().
      body(getFile("fund1.json")).
      contentType(JSON).
      accept("text/plain; charset=ISO-8859-1").
    when().
      post(funds).
    then().
      statusCode(201).
    extract().
      response();
    System.out.println(response.asString());

    //check that fund was inserted with get
    response =
    given().accept(TEXT).
    when().get(funds).
    then().
      body("total_records", equalTo(1)).
      body("funds[0].code", equalTo("MEDGRANT")).
    extract().response();
    System.out.println(response.asString());
    
    //get invoice test
    given().accept(TEXT).
    when().get(invoices).
    then().
      body("total_records", equalTo(0)).
      body("invoices", empty());
    
    //insert poline
    Response response2 =
    given().
      body(getFile("poline.json")).
      contentType(JSON).
      accept("text/plain").
    when().
      post(poLines).
    then().
      statusCode(201).
    extract().
      response();
    System.out.println(response2.asString());
    
    //check that the poline was inserted
    response2 =
    given().accept(TEXT).
    when().get(poLines).
    then().
      body("total_records", equalTo(1)).
      body("po_lines[0].po_number", equalTo("0987654321")).
    extract().response();
    System.out.println(response2.asString());

  }
  
  @After
  public void tearDown(TestContext context) {
    vertx.close(context.asyncAssertSuccess());
  }

}
