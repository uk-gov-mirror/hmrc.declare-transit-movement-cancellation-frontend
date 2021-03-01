#!/bin/bash

echo ""
echo "Applying migration CancellationReason"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /:lrn/cancellationReason                        controllers.CancellationReasonController.onPageLoad(lrn: LocalReferenceNumber, mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /:lrn/cancellationReason                        controllers.CancellationReasonController.onSubmit(lrn: LocalReferenceNumber, mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /:lrn/changeCancellationReason                  controllers.CancellationReasonController.onPageLoad(lrn: LocalReferenceNumber, mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /:lrn/changeCancellationReason                  controllers.CancellationReasonController.onSubmit(lrn: LocalReferenceNumber, mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "cancellationReason.title = cancellationReason" >> ../conf/messages.en
echo "cancellationReason.heading = cancellationReason" >> ../conf/messages.en
echo "cancellationReason.checkYourAnswersLabel = cancellationReason" >> ../conf/messages.en
echo "cancellationReason.error.required = Enter cancellationReason" >> ../conf/messages.en
echo "cancellationReason.error.length = CancellationReason must be 100 characters or less" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/self: Generators =>/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryCancellationReasonUserAnswersEntry: Arbitrary[(CancellationReasonPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        value <- arbitrary[CancellationReasonPage.type#Data].map(Json.toJson(_))";\
    print "      } yield (CancellationReasonPage, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitraryCancellationReasonUserAnswersEntry.arbitrary ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class CheckYourAnswersHelper/ {\
     print;\
     print "";\
     print "  def cancellationReason: Option[Row] = userAnswers.get(CancellationReasonPage) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"cancellationReason.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(lit\"$answer\"),";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = routes.CancellationReasonController.onPageLoad(lrn, CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"cancellationReason.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration CancellationReason completed"
