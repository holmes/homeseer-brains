import React, {Component, PropTypes} from 'react';
import './App.css';

import { Button, DropdownButton, ButtonGroup, MenuItem, Panel, Grid, Row, Col } from 'react-bootstrap';
import ToggleButton from 'react-toggle-button';

import "whatwg-fetch"

let sources = [{name: "TV", sourceId: 1}, {name: "Chromecast", sourceId: 2}];

function source(sourceId) {
  for (let s of sources) {
    // eslint-disable-next-line
    if (s.sourceId == sourceId) {
      return s
    }
  }
}


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
    const volumeLevel = this.state.volume;
    const powerValue = this.state.power;

    let panelHeader = (
        <Grid>
          <Row className="show-grid">
            <Col xs={10}>
              {this.state.zone.name}
            </Col>
            <Col xs={2}>
              <ToggleButton value={powerValue} onToggle={(value) => { this.powerToggled(value) }}/>
            </Col>
          </Row>
        </Grid>
    );

    let sourceVolume = (
        <Grid>
          <Row className="show-grid">
            <Col xs={6}>
              <DropdownButton
                  id="source-selector"
                  title={source(this.state.sourceId).name}
                  value={this.state.sourceId}
                  onSelect={(eventKey) => { this.sourceChanged(eventKey) }}
              >
                {sources.map(source => {
                  return (
                      <MenuItem
                          active={source.sourceId === this.state.sourceId}
                          eventKey={source.sourceId}
                          key={source.sourceId}
                          value={source.sourceId}>
                        {source.name}
                      </MenuItem>)
                })}
              </DropdownButton>
            </Col>
            <Col xs={6}>
              Volume: {volumeLevel}
            </Col>
          </Row>
        </Grid>
    );

    return (
        <Panel header={panelHeader}>

          {sourceVolume}

          <ButtonGroup justified bsSize="large">
            <ButtonGroup justified bsSize="large">
              <Button bsStyle="primary" onClick={this.volumeUp}>Up</Button>
            </ButtonGroup>
            <ButtonGroup justified bsSize="large">
              <Button bsStyle="success" onClick={this.volumeDown}>Down</Button>
            </ButtonGroup>
          </ButtonGroup>
        </Panel>
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
