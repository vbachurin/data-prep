function startTourIntro(){
  var introTourItems = introJs();
    introTourItems.setOptions({
    steps: [
        {
          element: '#help-import-local',
          title: 'Import local file button',
          intro: 'Click here to import your new Dataset (csv, xls, etc.) and start play with it. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis mollis augue a neque cursus ac blandit orci faucibus. Phasellus nec metus purus.',
          position: 'right'
        },
        {
          element: '#btn-toggle-right-panel',
          title: 'Show / Hide Left Hand Side Panel',
          intro: 'Show / Hide interesting information (owner, creation date, size, etc.) about the dataset you have selected. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis mollis augue a neque cursus ac blandit orci faucibus. Phasellus nec metus purus.',
          position: 'left'
        },
        {
            element: '#help-dataset-nav',
            title: 'Quick Data Access',
            intro: 'This is the place you can quickly access the dataset you are working on.',
            position: 'right'
        }
    ]
  });

    introTourItems.start();
}


