import React, {Component, PropTypes} from 'react';
import './App.css';

import {
  Button, DropdownButton, ButtonGroup, MenuItem, Panel, Grid, Row, Col
} from 'react-bootstrap';
import ToggleButton from 'react-toggle-button';

import "whatwg-fetch"

class App extends Component {
  constructor(props) {
    super(props);

    let remoteHost; // It's just so much easier to do it this way than inject it properly.
    if ( process.env.NODE_ENV === "development") {
      remoteHost = "http://localhost:4567/";
    } else {
      remoteHost = "http://192.168.100.5:8080/ponderosa/"
    }
    console.log("Using host at " + remoteHost);

    this.state = {
      loaded: false,
      baseUrl: remoteHost
    };
  }

  componentDidMount() {
    let thing = this;

    fetch(this.state.baseUrl, { mode: 'cors' } )
    .then(function(response) {
      return response.json()
    }).then(function(json) {
      thing.setState({
        loaded: true,
        sources: json.sources,
        zoneInformation: json.zoneInformation
      })
    }).catch(function(ex) {
      console.log('parsing failed', ex)
    });
  }

  render() {
    if (this.state.loaded) {
      return (
          <div className="App">
            {this.state.zoneInformation.map(zoneInfo => {
              return <ZoneInformation key={zoneInfo.zone.zoneId} zoneInfo={zoneInfo}
                                      baseUrl={this.state.baseUrl}
                                      sources={this.state.sources}/>
            })}
          </div>
      );
    } else {
      return (
          <div className="App">
            <h1>Loading...</h1>
          </div>
      );
    }
  }
}

// TODO I think it's time to move callbacks to App.
class ZoneInformation extends React.Component {
  constructor(props) {
    super(props);

    let zone = props.zoneInfo.zone;
    let sourceId = props.zoneInfo.source && props.zoneInfo.source.sourceId;
    let volume = props.zoneInfo.volume;
    let power = props.zoneInfo.power;

    this.state = {
      zone: zone,
      sourceId: sourceId,
      volume: volume,
      power: power,

      baseUrl: props.baseUrl,
      sources: props.sources
    };

    this.volumeUp = this.volumeUp.bind(this)
    this.volumeDown = this.volumeDown.bind(this)
  }

  source(sourceId) {
    for (let s of this.state.sources) {
      // eslint-disable-next-line
      if (s.sourceId == sourceId) {
        return s
      }
    }
  }

  sourceName() {
    if (this.state.sourceId === undefined || isNaN(this.state.sourceId)) {
      return ""
    } else {
      return this.source(this.state.sourceId).name
    }
  }

  sourceChanged(sourceId) {
    const url = `${this.state.baseUrl}api/audio/${this.state.zone.zoneId}/source/${sourceId}`;

    const thing = this;
    fetch(url, {
      method: "POST", mode: 'cors'
    }).then(function () {
      thing.setState({sourceId: sourceId})
    });
  }

  volumeChanged(volume) {
    const url = `${this.state.baseUrl}api/audio/${this.state.zone.zoneId}/volume/${volume}`;

    const thing = this;
    fetch(url, {
      method: "POST", mode: 'cors'
    }).then(function () {
      thing.setState({volume: volume})
    });
  }

  volumeUp() {
    const url = `${this.state.baseUrl}api/audio/${this.state.zone.zoneId}/volume/up`;

    const thing = this;
    fetch(url, {
      method: "POST", mode: 'cors'
    }).then(function () {
      thing.setState((prevState, props) => {
        return {volume: prevState.volume + 2};
      })
    });
  }

  volumeDown() {
    const url = `${this.state.baseUrl}api/audio/${this.state.zone.zoneId}/volume/down`;

    const thing = this;
    fetch(url, {
      method: "POST", mode: 'cors'
    }).then(function () {
      thing.setState((prevState, props) => {
        return {volume: prevState.volume - 2};
      })
    });
  }

  powerToggled(originalPower) {
    const power = !originalPower;
    const url = `${this.state.baseUrl}api/audio/${this.state.zone.zoneId}/power/${power}`;

    const thing = this;
    fetch(url, {
      method: "POST", mode: 'cors'
    }).then(function () {
      thing.setState({power: power})
    });
  }

  render() {
    const powerValue = this.state.power;

    let panelHeader = (
        <Grid>
          <Row className="show-grid">
            <Col xs={10}>
              <span className="zoneName">{this.state.zone.name}</span>
            </Col>
            <Col xs={2}>
              <ToggleButton value={powerValue} onToggle={(value) => {
                this.powerToggled(value)
              }}/>
            </Col>
          </Row>
        </Grid>
    );

    let id = this.state.zone.zoneId + "SourceSelector";
    let sourceVolume = (
      <DropdownButton
          id={id}
          className="source-selector"
          title={this.sourceName()}
          value={this.state.sourceId}
          onSelect={(eventKey) => {
            this.sourceChanged(eventKey)
          }}
      >
        {this.state.sources.map(source => {
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
    );

    return (
        <Panel header={panelHeader}>

          {sourceVolume}

          <ButtonGroup justified bsSize="large">
            <ButtonGroup justified bsSize="large">
              <Button bsStyle="success" onClick={this.volumeDown}>Volume Down</Button>
            </ButtonGroup>
            <ButtonGroup justified bsSize="large">
              <Button bsStyle="primary" onClick={this.volumeUp}>Volume Up</Button>
            </ButtonGroup>
          </ButtonGroup>
        </Panel>
    )
  };
}

ZoneInformation.propTypes = {
  zoneInfo: PropTypes.shape({
    zone: PropTypes.shape({
      name: PropTypes.string.isRequired, zoneId: PropTypes.number.isRequired
    }), //
    source: PropTypes.shape({
      name: PropTypes.string.isRequired, sourceId: PropTypes.number.isOptional
    }), //
    volume: PropTypes.string.isOptional, //
    power: PropTypes.string.isOptional
  })
};

export default App;
