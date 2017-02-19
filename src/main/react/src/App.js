import React, {Component, PropTypes} from 'react';
import './App.css';

import Slider from 'rc-slider';
import 'rc-slider/assets/index.css';

import ToggleButton from 'react-toggle-button'

import "whatwg-fetch"

let sources = {
  sources: [{name: "TV", sourceId: 1}, {name: "Chromecast", sourceId: 2},]
};

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
      zone: props.zoneInfo.zone,
      sourceId: props.zoneInfo.source.sourceId,
      volume: parseInt(props.zoneInfo.volume, 10),
      power: props.zoneInfo.power === "true"
    }

    this.volumeUp = this.volumeUp.bind(this)
    this.volumeDown = this.volumeDown.bind(this)
  }

  sourceChanged(sourceId) {
    console.log(sourceId);
    const url = `http://192.168.100.5:8080/ponderosa/api/audio/${this.state.zone.zoneId}/source/${sourceId}`;

    const thing = this;
    fetch(url, {
      method: "POST", mode: 'no-cors'
    }).then(function () {
      thing.setState({sourceId: sourceId})
    });
  }

  volumeChanged(volume) {
    const url = `http://192.168.100.5:8080/ponderosa/api/audio/${this.state.zone.zoneId}/volume/${volume}`;

    const thing = this;
    fetch(url, {
      method: "POST", mode: 'no-cors'
    }).then(function () {
      thing.setState({volume: volume})
    });
  }

  volumeUp() {
    const url = `http://192.168.100.5:8080/ponderosa/api/audio/${this.state.zone.zoneId}/volume/up`;

    const thing = this;
    fetch(url, {
      method: "POST", mode: 'no-cors'
    }).then(function () {
      console.log(thing.state.volume);
      thing.setState((prevState, props) => {
        return { volume: prevState.volume + 2 };
      })
    });
  }

  volumeDown() {
    const url = `http://192.168.100.5:8080/ponderosa/api/audio/${this.state.zone.zoneId}/volume/down`;

    const thing = this;
    fetch(url, {
      method: "POST", mode: 'no-cors'
    }).then(function () {
      console.log(thing.state.volume);
      thing.setState((prevState, props) => {
        return { volume: prevState.volume - 2 };
      })
    });
  }

  powerToggled(originalPower) {
    const power = !originalPower;
    const url = `http://192.168.100.5:8080/ponderosa/api/audio/${this.state.zone.zoneId}/power/${power}`;

    const thing = this;
    fetch(url, {
      method: "POST", mode: 'no-cors'
    }).then(function () {
      thing.setState({power: power})
    });
  }

  render() {
    console.log(this.state)
    const volumeLevel = this.state.volume;
    const powerValue = this.state.power;

    return (
        <div>
          <h1>Zone: {this.state.zone.name}</h1>

          <ToggleButton value={powerValue} onToggle={(value) => { this.powerToggled(value) }}/>

          <p>Source:
            <select value={this.state.sourceId} onChange={(event) => { this.sourceChanged(event.target.value) }}>
              {sources.sources.map(source => {
                return (
                    <option key={source.sourceId} value={source.sourceId}>{source.name}</option>)
              })}
            </select>
          </p>

          <p>Volume: {volumeLevel}</p>
          <button onClick={this.volumeUp}>Up</button>
          <button onClick={this.volumeDown}>Down</button>
          <Slider value={volumeLevel} onChange={(value) => { this.volumeChanged(value) }}/>
        </div>
    )
  };
}

ZoneInformation.propTypes = {
  zoneInfo: PropTypes.shape({
    zone: PropTypes.shape({
      name: PropTypes.string.isRequired, zoneId: PropTypes.string.isRequired
    }), //
    source: PropTypes.shape({
      name: PropTypes.string.isRequired, sourceId: PropTypes.string.isRequired
    }), //
    volume: PropTypes.string.isRequired, //
    power: PropTypes.string.isRequired
  })
};

export default App;
