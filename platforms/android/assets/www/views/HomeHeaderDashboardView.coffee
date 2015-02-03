class HomeHeaderDashboardView extends Backbone.View
#  el: '#content table tbody'
#  el: ''
  tagName: 'p'

  events:
      "click #newPatient": "displayUserRegistrationForm"
      "click #searchPatients": "searchPatients"

  render: =>
      @$el.html '<p><input type="text" id="keyword" placeholder=' + polyglot.t('enterCountryId') + '/>&nbsp;<a data-role="button" id="searchPatients" class="btn btn-primary btn-lg">' + polyglot.t('searchButton') + '</a>&nbsp;<a data-role="button" id="newPatient" class="btn btn-primary btn-lg">' + polyglot.t('newPatient') + '</a></p>'

  displayUserRegistrationForm: =>
      Coconut.trigger "displayUserRegistrationForm"

  searchPatients: =>
#      Coconut.trigger "searchPatients"
    keyword = $("#keyword").val()
    Coconut.Controller.displayPatientRecords(keyword)

