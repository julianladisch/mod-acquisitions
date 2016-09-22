package org.folio.rest.impl;

import java.util.List;

import javax.ws.rs.core.Response;

import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

import org.folio.rest.annotations.Validate;
import org.folio.rest.persist.MongoCRUD;
import org.folio.rest.tools.utils.OutStream;
import org.folio.rest.tools.messages.Messages;
import org.folio.rest.jaxrs.model.PoLine;
import org.folio.rest.jaxrs.model.PoLines;
import org.folio.rest.jaxrs.resource.POLinesResource;
import org.folio.rest.utils.Consts;

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
  public void postPoLines(String authorization, String lang, PoLine poLine, Handler<AsyncResult<Response>> asyncResultHandler,
      Context vertxContext) throws Exception {

    /**
     * http://HOST:PORT/apis/po_lines
     */

    try {
      System.out.println("sending... postPoLines");
      vertxContext.runOnContext(v -> {

        try {
          MongoCRUD.getInstance(vertxContext.owner())
              .save(Consts.POLINE_COLLECTION, poLine,
                  reply -> {
                    try {
                      String id = reply.result();
                      poLine.setId(id);
                      OutStream stream = new OutStream();
                      stream.setData(poLine);
                      asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(PostPoLinesResponse.withJsonCreated(
                          "po_lines/" + id, stream)));
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
