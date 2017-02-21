import React from 'react';
import ReactDOM from 'react-dom';
import { shallow, mount, render } from 'enzyme';
import App from './App';

let zoneInformation = {
  zones: [{
    zone: {name: "Family Room", zoneId: "1"},
    source: {name: "TV", sourceId: "1"},
    power: "true",
    volume: "26"
  }, {
    zone: {name: "Kitchen", zoneId: "2"},
    source: {name: "Chromecast", sourceId: "2"},
    power: "true",
    volume: "26"
  }, {
    zone: {name: "Outside", zoneId: "3"},
    source: {name: "Chromecast", sourceId: "2"},
    power: "false",
    volume: "60"
  }, {
    zone: {name: "Master", zoneId: "4"},
    source: {name: "Chromecast", sourceId: "2"},
    power: "false",
    volume: "30"
  }]
};


test('renders without crashing', () => {
  const div = document.createElement('div');
  ReactDOM.render(<App />, div);
});

// test( => {
//
// });
