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
    if (process.env.NODE_ENV === "development") {
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

    fetch(`${this.state.baseUrl}api/audio/zoneInfo`, { mode: 'cors' } )
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

function PanelHeader(props) {
  return (
      <Grid>
        <Row className="show-grid">
          <Col xs={10}>
            <span className="zoneName">{props.zoneName}</span>
          </Col>
          <Col xs={2}>
            <ToggleButton value={props.power} onToggle={props.onToggle} />
          </Col>
        </Row>
      </Grid>
  )
}

class SourceSelector extends React.Component {
  sourceName(sourceId) {
    if (sourceId === undefined || isNaN(sourceId)) {
      return ""
    }

    for (let s of this.props.sources) {
      // eslint-disable-next-line
      if (s.sourceId === sourceId) {
        return s.name
      }
    }
  }

  render() {
    return (
        <DropdownButton
            id={this.props.zoneId + "SourceSelector"}
            className="source-selector"
            title={this.sourceName(this.props.selectedSourceId)}
            value={this.props.selectedSourceId}
            onSelect={this.props.sourceChanged}
        >
          {this.props.sources.map(source => {
            return (
                <MenuItem
                    active={source.sourceId === this.props.selectedSourceId}
                    eventKey={source.sourceId}
                    key={source.sourceId}
                    value={source.sourceId}>
                  {source.name}
                </MenuItem>)
          })}
        </DropdownButton>
    );
  }
}

function VolumeSection(props) {
  return (
    <ButtonGroup justified bsSize="large">
      <ButtonGroup justified bsSize="large">
        <Button bsStyle="success" onClick={props.volumeDown}>Volume Down</Button>
      </ButtonGroup>
      <ButtonGroup justified bsSize="large">
        <Button bsStyle="primary" onClick={props.volumeUp}>Volume Up</Button>
      </ButtonGroup>
    </ButtonGroup>
  )
}

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
    };

    this.volumeUp = this.volumeUp.bind(this);
    this.volumeDown = this.volumeDown.bind(this);
    this.sourceChanged = this.sourceChanged.bind(this);
    this.powerToggled = this.powerToggled.bind(this);
  }

  sourceChanged(sourceId) {
    const url = `${this.props.baseUrl}api/audio/${this.state.zone.zoneId}/source/${sourceId}`;

    const thing = this;
    fetch(url, {
      method: "POST", mode: 'cors'
    }).then(function () {
      thing.setState({sourceId: sourceId})
    });
  }

  volumeUp() {
    const url = `${this.props.baseUrl}api/audio/${this.state.zone.zoneId}/volume/up`;

    const thing = this;
    fetch(url, {
      method: "POST", mode: 'cors'
    }).then(function () {
      thing.setState((prevState) => {
        return {
          power: true,
          volume: prevState.volume + 2
        };
      })
    });
  }

  volumeDown() {
    const url = `${this.props.baseUrl}api/audio/${this.state.zone.zoneId}/volume/down`;

    const thing = this;
    fetch(url, {
      method: "POST", mode: 'cors'
    }).then(function () {
      thing.setState((prevState) => {
        return {
          power: true,
          volume: prevState.volume - 2
        };
      })
    });
  }

  powerToggled(originalPower) {
    const power = !originalPower;
    const url = `${this.props.baseUrl}api/audio/${this.state.zone.zoneId}/power/${power}`;

    const thing = this;
    fetch(url, {
      method: "POST", mode: 'cors'
    }).then(function () {
      thing.setState({power: power})
    });
  }

  render() {
    let panelHeader = (
        <PanelHeader
            zoneName={this.state.zone.name}
            power={this.state.power}
            onToggle={this.powerToggled}
        />
    );

    return (
        <Panel header={panelHeader}>
          <SourceSelector
              zoneId={this.state.zone.zoneId}
              selectedSourceId={this.state.sourceId}
              sources={this.props.sources}
              sourceChanged={this.sourceChanged}
          />

          <VolumeSection
            volumeUp={this.volumeUp}
            volumeDown={this.volumeDown}
          />
        </Panel>
    )
  };
}

PanelHeader.propTypes = {
  zoneName: PropTypes.string.isRequired,
  power: PropTypes.bool,
  onToggle: PropTypes.func.isRequired
};

SourceSelector.propTypes = {
  zoneId: PropTypes.number.isRequired, //
  selectedSourceId: PropTypes.number, //
  sources: PropTypes.arrayOf(PropTypes.shape({
    name: PropTypes.string.isRequired, sourceId: PropTypes.number.isRequired
  })).isRequired,
  sourceChanged: PropTypes.func.isRequired
};

VolumeSection.propTypes = {
  volumeUp: PropTypes.func.isRequired,
  volumeDown: PropTypes.func.isRequired,
};

ZoneInformation.propTypes = {
  zoneInfo: PropTypes.shape({
    zone: PropTypes.shape({
      name: PropTypes.string.isRequired, zoneId: PropTypes.number.isRequired
    }).isRequired, //
    source: PropTypes.shape({
      name: PropTypes.string.isRequired, sourceId: PropTypes.number.isRequired
    }), //
    volume: PropTypes.number, //
    power: PropTypes.bool
  })
};

export default App;
