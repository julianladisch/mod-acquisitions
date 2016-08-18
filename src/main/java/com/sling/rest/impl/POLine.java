/**
 * POLine
 * 
 * Jun 26, 2016
 *
 * Apache License Version 2.0
 */
package com.sling.rest.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Handler;

import javax.ws.rs.core.Response;

import com.sling.rest.jaxrs.model.PoLine;
import com.sling.rest.jaxrs.resource.POLinesResource;

/**
 * @author shale
 *
 */
public class POLine implements POLinesResource {

  /* (non-Javadoc)
   * @see com.sling.rest.jaxrs.resource.POLinesResource#getPoLines(java.lang.String, java.lang.String, java.lang.String, com.sling.rest.jaxrs.resource.POLinesResource.Order, int, int, java.lang.String, io.vertx.core.Handler, io.vertx.core.Context)
   */
  @Override
  public void getPoLines(String authorization, String query, String orderBy, Order order, int offset, int limit, String lang,
      Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {
    // TODO Auto-generated method stub

  }

  /* (non-Javadoc)
   * @see com.sling.rest.jaxrs.resource.POLinesResource#postPoLines(java.lang.String, java.lang.String, com.sling.rest.jaxrs.model.PoLine, io.vertx.core.Handler, io.vertx.core.Context)
   */
  @Override
  public void postPoLines(String authorization, String lang, PoLine entity, Handler<AsyncResult<Response>> asyncResultHandler,
      Context vertxContext) throws Exception {
    // TODO Auto-generated method stub

  }

  /* (non-Javadoc)
   * @see com.sling.rest.jaxrs.resource.POLinesResource#getPoLinesByPoLineId(java.lang.String, java.lang.String, java.lang.String, io.vertx.core.Handler, io.vertx.core.Context)
   */
  @Override
  public void getPoLinesByPoLineId(String poLineId, String authorization, String lang, Handler<AsyncResult<Response>> asyncResultHandler,
      Context vertxContext) throws Exception {
    // TODO Auto-generated method stub

  }

  /* (non-Javadoc)
   * @see com.sling.rest.jaxrs.resource.POLinesResource#deletePoLinesByPoLineId(java.lang.String, java.lang.String, java.lang.String, io.vertx.core.Handler, io.vertx.core.Context)
   */
  @Override
  public void deletePoLinesByPoLineId(String poLineId, String authorization, String lang,
      Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {
    // TODO Auto-generated method stub

  }

  /* (non-Javadoc)
   * @see com.sling.rest.jaxrs.resource.POLinesResource#putPoLinesByPoLineId(java.lang.String, java.lang.String, java.lang.String, com.sling.rest.jaxrs.model.PoLine, io.vertx.core.Handler, io.vertx.core.Context)
   */
  @Override
  public void putPoLinesByPoLineId(String poLineId, String authorization, String lang, PoLine entity,
      Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {
    // TODO Auto-generated method stub

  }

}
