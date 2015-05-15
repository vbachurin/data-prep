function startTourNavBar(){
  var navBarItems = introJs();
  navBarItems.setOptions({
    steps: [
        {
          element: '#help-logo',
          title: 'Logo',
          intro: 'This is Talend logo!',
          position: 'right'
        },
        {
          element: '#help-mkt',
          title: 'Market place',
          intro: 'Click this to open market place.',
          position: 'bottom'
        }
    ]
  });

  navBarItems.start();
}

function startTourFooter(){
  var footerItems = introJs();
  footerItems.setOptions({
    steps: [
        {
          element: '#help-mail',
          title: 'A nice icon',
          intro: 'This is a nice mail icon',
          position: 'bottom'
        },
        {
          element: '#help-version',
          title: 'Version',
          intro: 'This is the current version',
          position: 'top'
        }
    ]
  });

  footerItems.start();
}

