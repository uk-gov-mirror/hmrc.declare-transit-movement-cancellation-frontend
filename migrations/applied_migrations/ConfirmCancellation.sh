#!/bin/bash

echo ""
echo "Applying migration ConfirmCancellation"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /:lrn/confirmCancellation                        controllers.ConfirmCancellationController.onPageLoad(lrn: LocalReferenceNumber, mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /:lrn/confirmCancellation                        controllers.ConfirmCancellationController.onSubmit(lrn: LocalReferenceNumber, mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /:lrn/changeConfirmCancellation                  controllers.ConfirmCancellationController.onPageLoad(lrn: LocalReferenceNumber, mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /:lrn/changeConfirmCancellation                  controllers.ConfirmCancellationController.onSubmit(lrn: LocalReferenceNumber, mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "confirmCancellation.title = confirmCancellation" >> ../conf/messages.en
echo "confirmCancellation.heading = confirmCancellation" >> ../conf/messages.en
echo "confirmCancellation.checkYourAnswersLabel = confirmCancellation" >> ../conf/messages.en
echo "confirmCancellation.error.required = Select yes if confirmCancellation" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/self: Generators =>/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryConfirmCancellationUserAnswersEntry: Arbitrary[(ConfirmCancellationPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        value <- arbitrary[ConfirmCancellationPage.type#Data].map(Json.toJson(_))";\
    print "      } yield (ConfirmCancellationPage, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitraryConfirmCancellationUserAnswersEntry.arbitrary ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class CheckYourAnswersHelper/ {\
     print;\
     print "";\
     print "  def confirmCancellation: Option[Row] = userAnswers.get(ConfirmCancellationPage) map {";\
     print "    answer =>";\
     print "      Row(";\
     print "        key     = Key(msg\"confirmCancellation.checkYourAnswersLabel\", classes = Seq(\"govuk-!-width-one-half\")),";\
     print "        value   = Value(yesOrNo(answer)),";\
     print "        actions = List(";\
     print "          Action(";\
     print "            content            = msg\"site.edit\",";\
     print "            href               = routes.ConfirmCancellationController.onPageLoad(lrn, CheckMode).url,";\
     print "            visuallyHiddenText = Some(msg\"site.edit.hidden\".withArgs(msg\"confirmCancellation.checkYourAnswersLabel\"))";\
     print "          )";\
     print "        )";\
     print "      )";\
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration ConfirmCancellation completed"
