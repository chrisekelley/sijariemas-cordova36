HomeSearchCompositeView = Backbone.Marionette.CompositeView.extend
  childView: HomeSearchRecordItemView,
  childViewContainer: '#records',
  template: JST["_attachments/templates/HomeSearchView.handlebars"]

