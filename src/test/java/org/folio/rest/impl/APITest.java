package org.folio.rest.impl;

import java.io.IOException;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;

import org.apache.commons.io.IOUtils;
import org.folio.rest.RestVerticle;
import org.folio.rest.persist.MongoCRUD;
import org.folio.rest.tools.utils.NetworkUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.config.EncoderConfig;
import com.jayway.restassured.response.Response;

import static com.jayway.restassured.http.ContentType.JSON;
import static com.jayway.restassured.http.ContentType.TEXT;
import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class APITest {
  private static Vertx vertx;

  private String path;
  private String arrayName;

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

  /**
   * Return the substring after the last slash.
   * Example: getIdFromLocationURI("vendors/883") = "883"
   * @param location  the location uri to extract the id from
   * @return the id
   */
  private String getIdFromLocationURI(String location) {
    int pos = location.lastIndexOf('/');
    if (pos < 0) {
      // a post to http://localhost/apis/vendors
      // returning
      // Location: 12345
      // results in wron http://localhost/apis/12345
      // but it should return
      // Location: vendors/12345
      // resulting in correct http://localhost/apis/vendors/12345
      // See URI-reference spec in https://tools.ietf.org/html/rfc7231#section-7.1.2

      throw new IllegalArgumentException("Location header must contain a / but is: " + location);
    }

    return location.substring(pos + 1);
  }

  private String getIdFromLocation(Response response) {
    return getIdFromLocationURI(response.getHeader("Location"));
  }

  private void emptyCollection() {
    given().accept(TEXT).
    when().get(path).
    then().
      statusCode(200).
      body("total_records", equalTo(0)).
      body(arrayName, empty());
  }

  private Response post(String json) {
    return
        given().contentType(JSON).body(json).accept("text/plain; charset=ISO-8859-1").
        when().post(path).
        then().
          statusCode(201).
        extract().response();
  }

  private Response post(String json, String predefinedId) {
    JsonObject jsonObject = new JsonObject(json);
    jsonObject.put("_id", predefinedId);
    Response response = post(jsonObject.encode());
    String id = getIdFromLocation(response);
    assertThat(id, is(predefinedId));
    return response;
  }

  private Response get1(String id) {
    return
        given().accept(TEXT).
        when().get(path).
        then().
          statusCode(200).
          body("total_records", equalTo(1)).
          body(arrayName + "[0]._id", equalTo(id)).
        extract().response();
  }

  private Response get(String id) {
    return
        given().accept(TEXT).
        when().get(path + '/' + id).
        then().
          statusCode(200).
          body("_id", equalTo(id)).
        extract().response();
  }

  private Response put(String id, String json) {
    return
        given().contentType(JSON).body(json).
        when().put(path + '/' + id).
        then().statusCode(204).
        extract().response();
  }

  private Response delete(String id) {
    return
        given().accept(TEXT).
        when().delete(path + '/' + id).
        then().statusCode(204).
        extract().response();
  }

  private Response getNonExisting(String id) {
    return
        given().accept(TEXT).
        when().get(path + '/' + id).
        then().statusCode(404).
        extract().response();
  }

  @Test
  public void testFunds() throws IOException {
    path = "/apis/funds";
    arrayName = "funds";
    String jsonFile = "fund1.json";

    emptyCollection();
    Response response = post(getFile(jsonFile));
    String id = getIdFromLocation(response);

    response = get1(id);
    response.then().body("funds[0].code", is("MEDGRANT"));

    response = get(id);
    response.then().body("code", is("MEDGRANT"));

    String json = getFile(jsonFile);
    json = json.replaceFirst("MEDGRANT", "poorEmptyFund");
    response = put(id, json);

    response = get1(id);
    response.then().body("funds[0].code", is("poorEmptyFund"));

    response = delete(id);
    emptyCollection();

    getNonExisting(id);

    String predefinedId = "11223344556677889900";
    response = post(getFile(jsonFile), predefinedId);
    response = get1(predefinedId);
    response.then().body("funds[0].code", is("MEDGRANT"));
  }

  @Test
  public void testInvoices() throws IOException {
    path = "/apis/invoices";
    arrayName = "invoices";
    String jsonFile = "invoice.json";

    emptyCollection();
    Response response = post(getFile(jsonFile));
    String id = getIdFromLocation(response);

    response = get1(id);
    response.then().body("invoices[0].vendor_invoice_number", is("1234567890"));

    response = get(id);
    response.then().body("vendor_invoice_number", is("1234567890"));

    String json = getFile(jsonFile);
    json = json.replaceFirst("1234567890", "9");
    response = put(id, json);

    response = get1(id);
    response.then().body("invoices[0].vendor_invoice_number", is("9"));

    response = delete(id);
    emptyCollection();

    getNonExisting(id);

    String predefinedId = "11223344556677889900";
    response = post(getFile(jsonFile), predefinedId);
    response = get1(predefinedId);
    response.then().body("invoices[0].vendor_invoice_number", is("1234567890"));
  }

  @Test
  public void testPoLines() throws IOException {
    path = "/apis/po_lines";
    arrayName = "po_lines";
    String jsonFile = "poline.json";

    emptyCollection();
    Response response = post(getFile(jsonFile));
    String id = getIdFromLocation(response);

    response = get1(id);
    response.then().body("po_lines[0].po_number", is("0987654321"));

    response = get(id);
    response.then().body("po_number", is("0987654321"));

    String json = getFile(jsonFile);
    json = json.replaceFirst("0987654321", "5");
    response = put(id, json);

    response = get1(id);
    response.then().body("po_lines[0].po_number", is("5"));

    response = delete(id);
    emptyCollection();

    getNonExisting(id);

    String predefinedId = "11223344556677889900";
    response = post(getFile(jsonFile), predefinedId);
    response = get1(predefinedId);
    response.then().body("po_lines[0].po_number", is("0987654321"));
  }

  @Test
  public void testVendor() throws IOException {
    path = "/apis/vendors";
    arrayName = "vendors";
    String jsonFile = "vendor.json";

    emptyCollection();
    Response response = post(getFile(jsonFile));
    String id = getIdFromLocation(response);

    response = get1(id);
    response.then().body("vendors[0].code", equalTo("YBP"));

    response = get(id);
    response.then().body("code", equalTo("YBP"));

    String json = getFile(jsonFile);
    json = json.replaceFirst("YBP", "foobar");
    response = put(id, json);

    response = get1(id);
    response.then().body("vendors[0].code", equalTo("foobar"));

    response = delete(id);
    emptyCollection();

    getNonExisting(id);

    String predefinedId = "11223344556677889900";
    response = post(getFile(jsonFile), predefinedId);
    response = get1(predefinedId);
    response.then().body("vendors[0].code", equalTo("YBP"));
  }

  @AfterClass
  public static void tearDownClass(TestContext context) {
    vertx.close(context.asyncAssertSuccess());
  }

}
