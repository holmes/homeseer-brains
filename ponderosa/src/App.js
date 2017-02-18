import React, {Component} from 'react';
import './App.css';

let sources = {
  sources: [{name: "TV", sourceId: 0}, {name: "Chromecast", sourceId: 1},]
};

let zoneInformation = {
  zones: [{
    zone: {name: "Family Room", zoneId: "0"},
    source: {name: "TV", sourceId: "0"},
    power: "true",
    volume: "30"
  }, {
    zone: {name: "Kitchen", zoneId: "1"},
    source: {name: "Chromecast", sourceId: "1"},
    power: "true",
    volume: "25"
  }, {
    zone: {name: "Outside", zoneId: "2"},
    source: {name: "Chromecast", sourceId: "1"},
    power: "false",
    volume: "30"
  }, {
    zone: {name: "Master", zoneId: "3"},
    source: {name: "Chromecast", sourceId: "1"},
    power: "false",
    volume: "30"
  }]
};

class App extends Component {
  render() {
    return (
        <div className="App">
          <ul>
            {zoneInformation.zones.map(zone => {
              return <ZoneInformation key={zone.zone.zoneId} zoneInfo={zone}/>
            })}
          </ul>
        </div>
    );
  }
}

class ZoneInformation extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      source: props.zoneInfo.source.sourceId
    }
  }

  render() {
    return (
        <div>
          <h1>Zone: {this.props.zoneInfo.zone.name}</h1>
          <p>Source:
            <select value={this.state.source} readOnly="true">
              {sources.sources.map(source => {
                return (
                    <option key={source.sourceId} value={source.sourceId}>{source.name}</option>)
              })}
            </select>
          </p>
          <p>Volume: {this.props.zoneInfo.volume}</p>
          <p>On/Off: {this.props.zoneInfo.power ? "On" : "Off"}</p>
        </div>
    )
  };
}

export default App;
