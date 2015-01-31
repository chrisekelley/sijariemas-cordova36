class ReportHeaderDashboardView extends Backbone.View
#  el: '#content table tbody'
#  el: ''
  tagName: 'p'

  events:
      "click #newPatient": "displayUserRegistrationForm"

  render: =>
      @$el.html '<h2>' + polyglot.t("HomeDashboardHeader") + '<h2>' + '<p><a data-role="button" id="newPatient" class="btn btn-primary btn-lg">' + polyglot.t('newPatient') + '</a></p>'

  displayUserRegistrationForm: =>
      Coconut.trigger "displayUserRegistrationForm"

