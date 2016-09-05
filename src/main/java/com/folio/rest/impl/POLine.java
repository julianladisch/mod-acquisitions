package com.folio.rest.impl;

import java.util.List;







import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Handler;




import io.vertx.core.json.JsonObject;

import javax.ws.rs.core.Response;







import com.sling.rest.annotations.Validate;
import com.sling.rest.persist.MongoCRUD;
import com.sling.rest.resource.utils.OutStream;
import com.sling.rest.tools.Messages;
import com.folio.rest.jaxrs.model.Invoice;
import com.folio.rest.jaxrs.model.Invoices;
import com.folio.rest.jaxrs.model.PoLine;
import com.folio.rest.jaxrs.model.PoLines;
import com.folio.rest.jaxrs.resource.POLinesResource;
import com.folio.rest.jaxrs.resource.InvoicesResource.DeleteInvoicesByInvoiceIdResponse;
import com.folio.rest.jaxrs.resource.InvoicesResource.GetInvoicesByInvoiceIdResponse;
import com.folio.rest.jaxrs.resource.InvoicesResource.GetInvoicesResponse;
import com.folio.rest.jaxrs.resource.InvoicesResource.PostInvoicesResponse;
import com.folio.rest.jaxrs.resource.InvoicesResource.PutInvoicesByInvoiceIdResponse;
import com.folio.rest.utils.Consts;


public class POLine implements POLinesResource {

  private final Messages            messages = Messages.getInstance();

  @Validate
  @Override
  public void getPoLines(String authorization, String query, String orderBy, Order order, int offset, int limit, String lang,
      Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {

    /**
     * http://HOST:PORT/apis/po_lines
     */
    
    System.out.println("sending... getPoLines");
    vertxContext.runOnContext(v -> {
      try {
        MongoCRUD.getInstance(vertxContext.owner()).get(
          MongoCRUD.buildJson(PoLine.class.getName(), Consts.POLINE_COLLECTION, query, orderBy, order, offset, limit),
            reply -> {
              try {
                PoLines polines = new PoLines();
                List<PoLine> polineList = (List<PoLine>)reply.result();
                polines.setPoLines(polineList);
                polines.setTotalRecords(polineList.size());
                asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(GetPoLinesResponse.withJsonOK(polines)));
              } catch (Exception e) {
                e.printStackTrace();
                asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(GetPoLinesResponse.withPlainInternalServerError(messages
                    .getMessage(lang, "10001"))));
              }
            });
      } catch (Exception e) {
        e.printStackTrace();
        asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(GetPoLinesResponse.withPlainInternalServerError(messages.getMessage(
            lang, "10001"))));
      }
    });
  }

  @Validate
  @Override
  public void postPoLines(String authorization, String lang, PoLine entity, Handler<AsyncResult<Response>> asyncResultHandler,
      Context vertxContext) throws Exception {

    /**
     * http://HOST:PORT/apis/po_lines
     */
    
    try {
      System.out.println("sending... postPoLines");
      vertxContext.runOnContext(v -> {

        try {
          MongoCRUD.getInstance(vertxContext.owner())
              .save(Consts.POLINE_COLLECTION, entity,
                  reply -> {
                    try {
                      PoLine p = new PoLine();
                      p = entity;
                      OutStream stream = new OutStream();
                      stream.setData(p);
                      asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(PostPoLinesResponse.withJsonCreated(reply.result(),
                          stream)));
                    } catch (Exception e) {
                      e.printStackTrace();
                      asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(PostPoLinesResponse
                          .withPlainInternalServerError(messages.getMessage(lang, "10001"))));
                    }
                  });
        } catch (Exception e) {
          e.printStackTrace();
          asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(PostPoLinesResponse.withPlainInternalServerError(messages
              .getMessage(lang, "10001"))));
        }
      });
    } catch (Exception e) {
      e.printStackTrace();
      asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(PostPoLinesResponse.withPlainInternalServerError(messages.getMessage(
          lang, "10001"))));
    }
    
    
  }


  @Validate
  @Override
  public void getPoLinesByPoLineId(String poLineId, String authorization, String lang, Handler<AsyncResult<Response>> asyncResultHandler,
      Context vertxContext) throws Exception {

    try {
      JsonObject q = new JsonObject();
      q.put("_id", poLineId);
      System.out.println("sending... getPoLinesByPoLineId");
      vertxContext.runOnContext(v -> {
        MongoCRUD.getInstance(vertxContext.owner()).get(
          MongoCRUD.buildJson(PoLine.class.getName(), Consts.POLINE_COLLECTION, q),
            reply -> {
              try {
                List<PoLine> poline = (List<PoLine>)reply.result();
                if (poline.size() == 0) {
                  asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(GetPoLinesByPoLineIdResponse.withPlainNotFound("Invoice: "
                      + messages.getMessage(lang, "10008"))));
                } else {
                  asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(GetPoLinesByPoLineIdResponse.withJsonOK(poline.get(0))));
                }
              } catch (Exception e) {
                e.printStackTrace();
                asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(GetPoLinesByPoLineIdResponse
                    .withPlainInternalServerError(messages.getMessage(lang, "10001"))));
              }
            });
      });
    } catch (Exception e) {
      e.printStackTrace();
      asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(GetPoLinesByPoLineIdResponse.withPlainInternalServerError(messages
          .getMessage(lang, "10001"))));
    }
    
  }


  @Validate
  @Override
  public void deletePoLinesByPoLineId(String poLineId, String authorization, String lang,
      Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {

    try {
      JsonObject q = new JsonObject();
      q.put("_id", poLineId);
      System.out.println("sending... deletePoLinesByPoLineId");
      vertxContext.runOnContext(v -> {
        MongoCRUD.getInstance(vertxContext.owner()).delete(Consts.POLINE_COLLECTION, poLineId,
            reply -> {
              try {
                asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(DeletePoLinesByPoLineIdResponse.withNoContent()));
              } catch (Exception e) {
                e.printStackTrace();
                asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(DeletePoLinesByPoLineIdResponse
                    .withPlainInternalServerError(messages.getMessage(lang, "10001"))));
              }
            });
      });
    } catch (Exception e) {
      e.printStackTrace();
      asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(DeletePoLinesByPoLineIdResponse.withPlainInternalServerError(messages
          .getMessage(lang, "10001"))));
    }
    
  }


  @Validate
  @Override
  public void putPoLinesByPoLineId(String poLineId, String authorization, String lang, PoLine entity,
      Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {

    try {
      JsonObject q = new JsonObject();
      q.put("_id", poLineId);
      System.out.println("sending... putPoLinesByPoLineId");
      vertxContext.runOnContext(v -> {
        MongoCRUD.getInstance(vertxContext.owner()).update(Consts.POLINE_COLLECTION, entity, q,
            reply -> {
              try {
                asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(PutPoLinesByPoLineIdResponse.withNoContent()));
              } catch (Exception e) {
                e.printStackTrace();
                asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(PutPoLinesByPoLineIdResponse
                    .withPlainInternalServerError(messages.getMessage(lang, "10001"))));
              }
            });
      });
    } catch (Exception e) {
      e.printStackTrace();
      asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(PutPoLinesByPoLineIdResponse.withPlainInternalServerError(messages
          .getMessage(lang, "10001"))));
    }   
    
  }

}
