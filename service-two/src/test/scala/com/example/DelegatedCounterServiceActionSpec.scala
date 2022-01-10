package com.example

import com.akkaserverless.scalasdk.action.Action
import com.akkaserverless.scalasdk.testkit.ActionResult
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

// This class was initially generated based on the .proto definition by Akka Serverless tooling.
//
// As long as this file exists it will not be overwritten: you can maintain it yourself,
// or delete it so it is regenerated as needed.

class DelegatedCounterServiceActionSpec
    extends AnyWordSpec
    with Matchers {

  "DelegatedCounterServiceAction" must {

    "have example test that can be removed" in {
      val testKit = DelegatedCounterServiceActionTestKit(new DelegatedCounterServiceAction(_))
      // use the testkit to execute a command
      // and verify final updated state:
      // val result = testKit.someOperation(SomeRequest)
      // verify the response
      // result.reply shouldBe expectedReply
    }

    "handle command IncreaseCounter" in {
      val testKit = DelegatedCounterServiceActionTestKit(new DelegatedCounterServiceAction(_))
      // val result = testKit.increaseCounter(IncreaseCounterRequest(...))
    }

  }
}
