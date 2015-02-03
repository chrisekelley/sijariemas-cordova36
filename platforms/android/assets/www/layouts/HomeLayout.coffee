HomeLayout = Backbone.Marionette.LayoutView.extend
    template: JST["_attachments/templates/HomeLayout.handlebars"],
    regions:
        homeHeaderRegion: "#home-header-region",
        homeListingRegion: "#home-listing-region"
