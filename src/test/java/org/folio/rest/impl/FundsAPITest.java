package org.folio.rest.impl;

import java.io.IOException;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import org.apache.commons.io.IOUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.config.EncoderConfig;
import com.jayway.restassured.response.Header;
import com.jayway.restassured.response.Response;

import org.folio.rest.RestVerticle;
import org.folio.rest.persist.MongoCRUD;
import org.folio.rest.tools.utils.NetworkUtils;

import static com.jayway.restassured.http.ContentType.JSON;
import static com.jayway.restassured.http.ContentType.TEXT;
import static com.jayway.restassured.RestAssured.given;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import static org.hamcrest.Matchers.containsString;
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
  /** vendors path */
  private static String vendors = "/apis/vendors";

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
  
  @BeforeClass
  public static void setUpClass(TestContext context) {
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
      deployRestVerticle(context);
    } catch (Exception e) {
      context.fail(e);
    }
  }

  private String getFile(String filename) throws IOException {
    return IOUtils.toString(getClass().getClassLoader().getResourceAsStream(filename), "UTF-8");
  }
  
  @Test
  public void testFunds() throws IOException {
    //get funds before inserting
    given().accept(TEXT).
    when().get(funds).
    then().
      body("total_records", equalTo(0)).
      body("funds", empty());
    
    //insert fund
    Response response =  // use System.out.println(response.asString()) to inspect.
    given().
      body(getFile("fund1.json")).
      contentType(JSON).
      accept("text/plain; charset=ISO-8859-1").
    when().
      post(funds).
    then().
      statusCode(201).
    extract().response();

    //check that fund was inserted with get
    response =
    given().accept(TEXT).
    when().get(funds).
    then().
      body("total_records", equalTo(1)).
      body("funds[0].code", equalTo("MEDGRANT")).
    extract().response();
  }

  @Test
  public void testInvoices() {
    // empty database without invoices
    given().accept(TEXT).
    when().get(invoices).
    then().
      body("total_records", equalTo(0)).
      body("invoices", empty());
  }

  @Test
  public void testPoLines() throws IOException {
    //insert poline.json
    Response response =
    given().
      body(getFile("poline.json")).
      contentType(JSON).
      accept("text/plain").
    when().
      post(poLines).
    then().
      statusCode(201).
    extract().response();
    
    //check that the poline was inserted
    response =
    given().accept(TEXT).
    when().get(poLines).
    then().
      body("total_records", equalTo(1)).
      body("po_lines[0].po_number", equalTo("0987654321")).
    extract().response();
  }
  
  /**
   * Return the substring after the last slash.
   * Example: getIdFromLocationURI("vendors/883") = "883"
   * @param location  the location uri to extract the id from
   * @return the id
   */
  public String getIdFromLocationURI(String location) {
    int pos = location.lastIndexOf('/');
    if (pos < 0) {
      // a post to http://localhost/apis/vendors
      // returning
      // Location: 12345
      // results in http://localhost/apis/12345
      // but it should return
      // Location: vendors/12345
      // resulting in http://localhost/apis/vendors/12345
      // See URI-reference spec in https://tools.ietf.org/html/rfc7231#section-7.1.2

      throw new IllegalArgumentException("Location header must contain a / but is: " + location);
    }

    return location.substring(pos + 1);
  }

  @Test
  public void testVendor() throws IOException {
    String vendorJson = getFile("vendor.json");

    // Empty database with no vendors
    given().accept(TEXT).
    when().get(vendors).
    then().
      statusCode(200).
      body("total_records", equalTo(0)).
      body("vendors", empty());

    //insert vendor
    Response response =
        given().contentType(JSON).body(vendorJson).accept("text/plain; charset=ISO-8859-1").
        when().post(vendors).
        then().
          statusCode(201).
          header("Location", containsString("/")).
        extract().response();

    String id = getIdFromLocationURI(response.getHeader("Location"));

    // check that vendor was inserted
    response =
        given().accept(TEXT).
        when().get(vendors).
        then().
          statusCode(200).
          body("total_records", equalTo(1)).
          body("vendors[0].code", equalTo("YBP")).
          body("vendors[0]._id", equalTo(id)).
        extract().response();

    // get by id
    response =
        given().accept(TEXT).
        when().get(vendors + '/' + id).
        then().
          statusCode(200).
          body("code", equalTo("YBP")).
          body("_id", equalTo(id)).
        extract().response();

    // alter vendor code
    vendorJson = vendorJson.replaceFirst("YBP", "foobar");
    response =
        given().contentType(JSON).body(vendorJson).
        when().put(vendors + '/' + id).
        then().statusCode(204).
        extract().response();

    // check new vendor code
    response =
        given().accept(TEXT).
        when().get(vendors).
        then().
          statusCode(200).
          body("total_records", equalTo(1)).
          body("vendors[0].code", equalTo("foobar")).
          body("vendors[0]._id", equalTo(id)).
        extract().response();

    // delete by id
    response =
        given().accept(TEXT).
        when().delete(vendors + '/' + id).
        then().statusCode(204).
        extract().response();

    // now empty again
    given().accept(TEXT).
    when().get(vendors).
    then().
      statusCode(200).
      body("total_records", equalTo(0)).
      body("vendors", empty());

    // get by id, not found
    response =
        given().accept(TEXT).
        when().get(vendors + '/' + id).
        then().statusCode(404).
        extract().response();
  }

  @AfterClass
  public static void tearDownClass(TestContext context) {
    vertx.close(context.asyncAssertSuccess());
  }

}
