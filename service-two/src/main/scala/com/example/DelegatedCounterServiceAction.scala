package com.example

import com.akkaserverless.scalasdk.action.Action
import com.akkaserverless.scalasdk.action.ActionCreationContext
import com.google.protobuf.empty.Empty

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class DelegatedCounterServiceAction(creationContext: ActionCreationContext) extends AbstractDelegatedCounterServiceAction {

  override def increaseCounter(increaseCounterRequest: IncreaseCounterRequest): Action.Effect[IncreaseCounterResult] = {
    // lookup the other service by deployed name
    val serviceOne = actionContext.getGrpcClient(classOf[CounterService], "service-one")
    val increaseResponse: Future[Empty] = serviceOne.increase(IncreaseValue(increaseCounterRequest.counterId, value = 1))
    val response = increaseResponse.map(empty => IncreaseCounterResult())
    effects.asyncReply(response)
  }
}

