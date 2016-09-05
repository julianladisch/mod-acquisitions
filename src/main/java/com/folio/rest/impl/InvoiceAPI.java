package com.folio.rest.impl;

import java.util.List;

import io.vertx.core.AsyncResult;
import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sling.rest.annotations.Validate;
import com.folio.rest.jaxrs.model.Fund;
import com.folio.rest.jaxrs.model.Funds;
import com.folio.rest.jaxrs.model.Invoice;
import com.folio.rest.jaxrs.model.InvoiceLine;
import com.folio.rest.jaxrs.model.InvoiceLines;
import com.folio.rest.jaxrs.model.Invoices;
import com.folio.rest.jaxrs.resource.InvoicesResource;
import com.folio.rest.jaxrs.resource.FundsResource.DeleteFundsByFundIdResponse;
import com.folio.rest.jaxrs.resource.FundsResource.GetFundsByFundIdResponse;
import com.folio.rest.jaxrs.resource.FundsResource.GetFundsResponse;
import com.folio.rest.jaxrs.resource.FundsResource.PostFundsResponse;
import com.folio.rest.jaxrs.resource.FundsResource.PutFundsByFundIdResponse;
import com.sling.rest.persist.MongoCRUD;
import com.sling.rest.resource.utils.OutStream;
import com.sling.rest.resource.utils.RestUtils;
import com.sling.rest.tools.Messages;
import com.folio.rest.utils.Consts;

public class InvoiceAPI implements InvoicesResource {


  private final Messages            messages = Messages.getInstance();
  
  @Validate
  @Override
  public void getInvoices(String authorization, String query, String orderBy, Order order, int offset, int limit, String lang,
      Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {

    System.out.println("sending... getInvoices");
    vertxContext.runOnContext(v -> {
      try {
        MongoCRUD.getInstance(vertxContext.owner()).get(
          MongoCRUD.buildJson(Invoice.class.getName(), Consts.INVOICE_COLLECTION, query, orderBy, order, offset, limit),
            reply -> {
              try {
                Invoices invoices = new Invoices();
                List<Invoice> invoiceObj = (List<Invoice>)reply.result();
                invoices.setInvoices(invoiceObj);
                invoices.setTotalRecords(invoiceObj.size());
                asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(GetInvoicesResponse.withJsonOK(invoices)));
              } catch (Exception e) {
                e.printStackTrace();
                asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(GetInvoicesResponse.withPlainInternalServerError(messages
                    .getMessage(lang, "10001"))));
              }
            });
      } catch (Exception e) {
        e.printStackTrace();
        asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(GetInvoicesResponse.withPlainInternalServerError(messages.getMessage(
            lang, "10001"))));
      }
    });
    
  }
  @Validate
  @Override
  public void postInvoices(String authorization, String lang, Invoice entity, Handler<AsyncResult<Response>> asyncResultHandler,
      Context vertxContext) throws Exception {

    try {
      System.out.println("sending... postInvoices");
      vertxContext.runOnContext(v -> {

        try {
          MongoCRUD.getInstance(vertxContext.owner())
              .save(Consts.INVOICE_COLLECTION, entity,
                  reply -> {
                    try {
                      Invoice p = new Invoice();
                      p = entity;
                      OutStream stream = new OutStream();
                      stream.setData(p);
                      asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(PostInvoicesResponse.withJsonCreated(reply.result(),
                          stream)));
                    } catch (Exception e) {
                      e.printStackTrace();
                      asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(PostInvoicesResponse
                          .withPlainInternalServerError(messages.getMessage(lang, "10001"))));
                    }
                  });
        } catch (Exception e) {
          e.printStackTrace();
          asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(PostInvoicesResponse.withPlainInternalServerError(messages
              .getMessage(lang, "10001"))));
        }
      });
    } catch (Exception e) {
      e.printStackTrace();
      asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(PostInvoicesResponse.withPlainInternalServerError(messages.getMessage(
          lang, "10001"))));
    }
    
  }
  @Validate
  @Override
  public void getInvoicesByInvoiceId(String invoiceId, String authorization, String lang,
      Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {

    try {
      JsonObject q = new JsonObject();
      q.put("_id", invoiceId);
      System.out.println("sending... getInvoicesByInvoiceId");
      vertxContext.runOnContext(v -> {
        MongoCRUD.getInstance(vertxContext.owner()).get(
          MongoCRUD.buildJson(Invoice.class.getName(), Consts.INVOICE_COLLECTION, q),
            reply -> {
              try {
                List<Invoice> invoice = (List<Invoice>)reply.result();
                if (invoice.size() == 0) {
                  asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(GetInvoicesByInvoiceIdResponse.withPlainNotFound("Invoice: "
                      + messages.getMessage(lang, "10008"))));
                } else {
                  asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(GetInvoicesByInvoiceIdResponse.withJsonOK(invoice.get(0))));
                }
              } catch (Exception e) {
                e.printStackTrace();
                asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(GetInvoicesByInvoiceIdResponse
                    .withPlainInternalServerError(messages.getMessage(lang, "10001"))));
              }
            });
      });
    } catch (Exception e) {
      e.printStackTrace();
      asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(GetInvoicesByInvoiceIdResponse.withPlainInternalServerError(messages
          .getMessage(lang, "10001"))));
    }
    
  }
  @Validate
  @Override
  public void deleteInvoicesByInvoiceId(String invoiceId, String authorization, String lang,
      Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {

    try {
      JsonObject q = new JsonObject();
      q.put("_id", invoiceId);
      System.out.println("sending... deleteInvoicesByInvoiceId");
      vertxContext.runOnContext(v -> {
        MongoCRUD.getInstance(vertxContext.owner()).delete(Consts.INVOICE_COLLECTION, invoiceId,
            reply -> {
              try {
                asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(DeleteInvoicesByInvoiceIdResponse.withNoContent()));
              } catch (Exception e) {
                e.printStackTrace();
                asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(DeleteInvoicesByInvoiceIdResponse
                    .withPlainInternalServerError(messages.getMessage(lang, "10001"))));
              }
            });
      });
    } catch (Exception e) {
      e.printStackTrace();
      asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(DeleteInvoicesByInvoiceIdResponse.withPlainInternalServerError(messages
          .getMessage(lang, "10001"))));
    }
    
  }
  @Validate
  @Override
  public void putInvoicesByInvoiceId(String invoiceId, String authorization, String lang, Invoice entity,
      Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {

    try {
      JsonObject q = new JsonObject();
      q.put("_id", invoiceId);
      System.out.println("sending... putInvoicesByInvoiceId");
      vertxContext.runOnContext(v -> {
        MongoCRUD.getInstance(vertxContext.owner()).update(Consts.INVOICE_COLLECTION, entity, q,
            reply -> {
              try {
                asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(PutInvoicesByInvoiceIdResponse.withNoContent()));
              } catch (Exception e) {
                e.printStackTrace();
                asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(PutInvoicesByInvoiceIdResponse
                    .withPlainInternalServerError(messages.getMessage(lang, "10001"))));
              }
            });
      });
    } catch (Exception e) {
      e.printStackTrace();
      asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(PutInvoicesByInvoiceIdResponse.withPlainInternalServerError(messages
          .getMessage(lang, "10001"))));
    }    
  }
  

  @Validate
  @Override
  public void getInvoicesByInvoiceIdInvoiceLines(String invoiceId, String authorization, String query, String orderBy, Order order,
      int offset, int limit, String lang, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {

    /**
     * http://HOST:PORT/apis/invoices/{invoiceId}/invoice_lines
     */
    
    try {

      System.out.println("sending... getInvoicesByInvoiceIdInvoiceLines");
      vertxContext.runOnContext(v -> {
        JsonObject q = new JsonObject();
        if(query != null){
          q = new JsonObject(query);          
        }
        q.put("invoice_id", invoiceId);
        MongoCRUD.getInstance(vertxContext.owner()).get(
          MongoCRUD.buildJson(InvoiceLine.class.getName(), Consts.INVOICE_LINE_COLLECTION, q),
            reply -> {
              try {
                InvoiceLines lines = new InvoiceLines();
                List<InvoiceLine> invoiceLine = (List<InvoiceLine>)reply.result();
                lines.setInvoiceLines(invoiceLine);
                lines.setTotalRecords(invoiceLine.size());
                asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(GetInvoicesByInvoiceIdInvoiceLinesResponse.withJsonOK(lines)));
                
              } catch (Exception e) {
                e.printStackTrace();
                asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(GetInvoicesByInvoiceIdInvoiceLinesResponse
                    .withPlainInternalServerError(messages.getMessage(lang, "10001"))));
              }
            });
      });
    } catch (Exception e) {
      e.printStackTrace();
      asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(GetInvoicesByInvoiceIdInvoiceLinesResponse.withPlainInternalServerError(messages
          .getMessage(lang, "10001"))));
    }  
  }
  
  @Validate
  @Override
  public void postInvoicesByInvoiceIdInvoiceLines(String invoiceId, String authorization, String lang, InvoiceLine entity,
      Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {
    
    /**
     * http://HOST:PORT/apis/invoices/{invoiceId}/invoice_lines
     */
    
    try {
      System.out.println("sending... postInvoicesByInvoiceIdInvoiceLines");
      vertxContext.runOnContext(v -> {

        try {
          MongoCRUD.getInstance(vertxContext.owner())
              .save(Consts.INVOICE_LINE_COLLECTION, entity,
                  reply -> {
                    try {
                      InvoiceLine p = new InvoiceLine();
                      p = entity;
                      p.setInvoiceId(invoiceId);
                      OutStream stream = new OutStream();
                      stream.setData(p);
                      asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(PostInvoicesByInvoiceIdInvoiceLinesResponse.withJsonCreated(reply.result(),
                          stream)));
                    } catch (Exception e) {
                      e.printStackTrace();
                      asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(PostInvoicesByInvoiceIdInvoiceLinesResponse
                          .withPlainInternalServerError(messages.getMessage(lang, "10001"))));
                    }
                  });
        } catch (Exception e) {
          e.printStackTrace();
          asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(PostInvoicesByInvoiceIdInvoiceLinesResponse.withPlainInternalServerError(messages
              .getMessage(lang, "10001"))));
        }
      });
    } catch (Exception e) {
      e.printStackTrace();
      asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(PostInvoicesByInvoiceIdInvoiceLinesResponse.withPlainInternalServerError(messages.getMessage(
          lang, "10001"))));
    } 
  }
  
  @Validate
  @Override
  public void getInvoicesByInvoiceIdInvoiceLinesByInvoiceLineId(String invoiceLineId, String invoiceId, String authorization, String lang,
      Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {

    /**
     * http://HOST:PORT/apis/invoices/{invoiceId}/invoice_lines/{invoiceLineId}
     */
    
    try {
      JsonObject q = new JsonObject();
      q.put("invoice_id", invoiceId);
      q.put("_id", invoiceLineId);
      System.out.println("sending... getInvoicesByInvoiceIdInvoiceLines");
      vertxContext.runOnContext(v -> {
        MongoCRUD.getInstance(vertxContext.owner()).get(
          MongoCRUD.buildJson(InvoiceLine.class.getName(), Consts.INVOICE_LINE_COLLECTION, q),
            reply -> {
              try {
                InvoiceLines lines = new InvoiceLines();
                List<InvoiceLine> invoiceLine = (List<InvoiceLine>)reply.result();;
                lines.setInvoiceLines(invoiceLine);
                lines.setTotalRecords(invoiceLine.size());
                asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(GetInvoicesByInvoiceIdInvoiceLinesResponse.withJsonOK(lines)));
                
              } catch (Exception e) {
                e.printStackTrace();
                asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(GetInvoicesByInvoiceIdInvoiceLinesResponse
                    .withPlainInternalServerError(messages.getMessage(lang, "10001"))));
              }
            });
      });
    } catch (Exception e) {
      e.printStackTrace();
      asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(GetInvoicesByInvoiceIdInvoiceLinesResponse.withPlainInternalServerError(messages
          .getMessage(lang, "10001"))));
    }
    
  }
  @Validate
  @Override
  public void deleteInvoicesByInvoiceIdInvoiceLinesByInvoiceLineId(String invoiceLineId, String invoiceId, String authorization,
      String lang, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {

    
    /**
     * http://HOST:PORT/apis/invoices/{invoiceId}/invoice_lines/{invoiceLineId}
     */
    
    try {
      JsonObject q = new JsonObject();
      q.put("invoice_id", invoiceId);
      q.put("_id", invoiceLineId);
      System.out.println("sending... deleteInvoicesByInvoiceIdInvoiceLinesByInvoiceLineId");
      vertxContext.runOnContext(v -> {
        MongoCRUD.getInstance(vertxContext.owner()).delete(Consts.INVOICE_LINE_COLLECTION, q, 
            reply -> {
              try {
                if(reply.succeeded()){
                  asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(
                    DeleteInvoicesByInvoiceIdInvoiceLinesByInvoiceLineIdResponse.withNoContent()));
                }
                else{
                  asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(DeleteInvoicesByInvoiceIdInvoiceLinesByInvoiceLineIdResponse
                    .withPlainInternalServerError(messages.getMessage(lang, "10001")))); 
                }                
              } catch (Exception e) {
                e.printStackTrace();
                asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(DeleteInvoicesByInvoiceIdInvoiceLinesByInvoiceLineIdResponse
                    .withPlainInternalServerError(messages.getMessage(lang, "10001"))));
              }
            });
      });
    } catch (Exception e) {
      e.printStackTrace();
      asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(DeleteInvoicesByInvoiceIdInvoiceLinesByInvoiceLineIdResponse
        .withPlainInternalServerError(messages.getMessage(lang, "10001"))));
    }
  }
  
  
  @Validate
  @Override
  public void putInvoicesByInvoiceIdInvoiceLinesByInvoiceLineId(String invoiceLineId, String invoiceId, String authorization, String lang,
      InvoiceLine entity, Handler<AsyncResult<Response>> asyncResultHandler, Context vertxContext) throws Exception {

    /**
     * http://HOST:PORT/apis/invoices/{invoiceId}/invoice_lines/{invoiceLineId}
     */
    
    try {
      JsonObject q = new JsonObject();
      q.put("invoice_id", invoiceId);
      q.put("_id", invoiceLineId);
      System.out.println("sending... putInvoicesByInvoiceIdInvoiceLinesByInvoiceLineId");
      vertxContext.runOnContext(v -> {
        MongoCRUD.getInstance(vertxContext.owner()).update(Consts.INVOICE_LINE_COLLECTION, entity, 
          q, reply -> {
              try {
                if(reply.succeeded()){
                  asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(
                    PutInvoicesByInvoiceIdInvoiceLinesByInvoiceLineIdResponse.withNoContent()));
                }
                else{
                  asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(PutInvoicesByInvoiceIdInvoiceLinesByInvoiceLineIdResponse
                    .withPlainInternalServerError(messages.getMessage(lang, "10001")))); 
                }                
              } catch (Exception e) {
                e.printStackTrace();
                asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(PutInvoicesByInvoiceIdInvoiceLinesByInvoiceLineIdResponse
                    .withPlainInternalServerError(messages.getMessage(lang, "10001"))));
              }
            });
      });
    } catch (Exception e) {
      e.printStackTrace();
      asyncResultHandler.handle(io.vertx.core.Future.succeededFuture(PutInvoicesByInvoiceIdInvoiceLinesByInvoiceLineIdResponse
        .withPlainInternalServerError(messages.getMessage(lang, "10001"))));
    }
    
    
  }

}
