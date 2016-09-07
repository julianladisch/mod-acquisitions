package com.folio.rest.impl;

import java.util.List;

import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import javax.ws.rs.core.Response;
import com.folio.rest.annotations.Validate;
import com.folio.rest.jaxrs.model.Fund;
import com.folio.rest.jaxrs.model.Funds;
import com.folio.rest.jaxrs.resource.FundsResource;
import com.folio.rest.utils.Consts;
import com.folio.rest.persist.MongoCRUD;
import com.folio.rest.tools.utils.OutStream;
import com.folio.rest.tools.Messages;

public class FundsAPI implements FundsResource {

  private final Messages            messages = Messages.getInstance();

  @Validate
  @Override
  public void getFunds(String authorization, String query, String orderBy, Order order, int offset, int limit, String lang,
      Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {

    System.out.println("sending... getFunds");
    vertxContext.runOnContext(v -> {
      try {
        MongoCRUD.getInstance(vertxContext.owner()).get(
          MongoCRUD.buildJson(Fund.class.getName(), Consts.FUNDS_COLLECTION, query, orderBy, order, offset, limit),
            reply -> {
              try {
                Funds funds = new Funds();
                // this is wasteful!!!
                List<Fund> fundObj = (List<Fund>)reply.result();
                funds.setFunds(fundObj);
                funds.setTotalRecords(fundObj.size());
                asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(GetFundsResponse.withJsonOK(funds)));
              } catch (Exception e) {
                e.printStackTrace();
                asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(GetFundsResponse.withPlainInternalServerError(messages
                    .getMessage(lang, "10001"))));
              }
            });
      } catch (Exception e) {
        e.printStackTrace();
        asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(GetFundsResponse.withPlainInternalServerError(messages.getMessage(
            lang, "10001"))));
      }
    });
    
  }
  
  @Validate
  @Override
  public void postFunds(String authorization, String lang, Fund entity, Handler<AsyncResult<Response>> asyncResultHandler,
      Context vertxContext) throws Exception {

    try {
      System.out.println("sending... postFunds");
      vertxContext.runOnContext(v -> {

        try {
          MongoCRUD.getInstance(vertxContext.owner())
              .save(Consts.FUNDS_COLLECTION, entity,
                  reply -> {
                    try {
                      Fund p = new Fund();
                      p = entity;
                      //p.setPatronId(reply.result());
                      OutStream stream = new OutStream();
                      stream.setData(p);
                      asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(PostFundsResponse.withJsonCreated(reply.result(),
                          stream)));
                    } catch (Exception e) {
                      e.printStackTrace();
                      asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(PostFundsResponse
                          .withPlainInternalServerError(messages.getMessage(lang, "10001"))));
                    }
                  });
        } catch (Exception e) {
          e.printStackTrace();
          asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(PostFundsResponse.withPlainInternalServerError(messages
              .getMessage(lang, "10001"))));
        }
      });
    } catch (Exception e) {
      e.printStackTrace();
      asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(PostFundsResponse.withPlainInternalServerError(messages.getMessage(
          lang, "10001"))));
    }
  }
  
  @Validate
  @Override
  public void getFundsByFundId(String fundId, String authorization, String lang, Handler<AsyncResult<Response>> asyncResultHandler,
      Context vertxContext) throws Exception {

    try {
      JsonObject q = new JsonObject();
      q.put("_id", fundId);
      System.out.println("sending... getFundsByFundId");
      vertxContext.runOnContext(v -> {
        MongoCRUD.getInstance(vertxContext.owner()).get(
          MongoCRUD.buildJson(Fund.class.getName(), Consts.FUNDS_COLLECTION, q),
            reply -> {
              try {
                List<Fund> funds = (List<Fund>)reply.result();
                if (funds.size() == 0) {
                  asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(GetFundsByFundIdResponse.withPlainNotFound("Patron"
                      + messages.getMessage(lang, "10008"))));
                } else {
                  asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(GetFundsByFundIdResponse.withJsonOK(funds.get(0))));
                }
              } catch (Exception e) {
                e.printStackTrace();
                asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(GetFundsByFundIdResponse
                    .withPlainInternalServerError(messages.getMessage(lang, "10001"))));
              }
            });
      });
    } catch (Exception e) {
      e.printStackTrace();
      asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(GetFundsByFundIdResponse.withPlainInternalServerError(messages
          .getMessage(lang, "10001"))));
    }
  }
  
  @Validate
  @Override
  public void deleteFundsByFundId(String fundId, String authorization, String lang, Handler<AsyncResult<Response>> asyncResultHandler,
      Context vertxContext) throws Exception {

    try {
      JsonObject q = new JsonObject();
      q.put("_id", fundId);
      System.out.println("sending... deleteFundsByFundId");
      vertxContext.runOnContext(v -> {
        MongoCRUD.getInstance(vertxContext.owner()).delete(Consts.FUNDS_COLLECTION, fundId,
            reply -> {
              try {
                asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(DeleteFundsByFundIdResponse.withNoContent()));
              } catch (Exception e) {
                e.printStackTrace();
                asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(DeleteFundsByFundIdResponse
                    .withPlainInternalServerError(messages.getMessage(lang, "10001"))));
              }
            });
      });
    } catch (Exception e) {
      e.printStackTrace();
      asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(DeleteFundsByFundIdResponse.withPlainInternalServerError(messages
          .getMessage(lang, "10001"))));
    }
  }

  @Validate
  @Override
  public void putFundsByFundId(String fundId, String authorization, String lang, Fund entity,
      Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {

    try {
      JsonObject q = new JsonObject();
      q.put("_id", fundId);
      System.out.println("sending... putPatronsByPatronId");
      vertxContext.runOnContext(v -> {
        MongoCRUD.getInstance(vertxContext.owner()).update(Consts.FUNDS_COLLECTION, entity, q,
            reply -> {
              try {
                asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(PutFundsByFundIdResponse.withNoContent()));
              } catch (Exception e) {
                e.printStackTrace();
                asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(PutFundsByFundIdResponse
                    .withPlainInternalServerError(messages.getMessage(lang, "10001"))));
              }
            });
      });
    } catch (Exception e) {
      e.printStackTrace();
      asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(PutFundsByFundIdResponse.withPlainInternalServerError(messages
          .getMessage(lang, "10001"))));
    }
    
  }

}
