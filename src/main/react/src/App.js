import React, {Component, PropTypes} from 'react';
import './App.css';

import {
  Button, DropdownButton, ButtonGroup, MenuItem, Panel, Grid, Row, Col, FormGroup,
  ControlLabel, Glyphicon
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
              return <ZoneInformation key={zoneInfo.zone.zoneId}
                                      zoneInfo={zoneInfo}
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

function AdvancedPanel(props) {
  return (
    <Panel className="advancedPanel" collapsible expanded={props.visible}>
        <FormGroup controlId="balance">
          <Col className="adjustmentLabel" componentClass={ControlLabel} sm={2}>Balance</Col>
          <Col sm={10}>
            <ButtonGroup justified bsSize="large" className="adjustmentGroup">
              <ButtonGroup justified bsSize="large">
                <Button bsStyle="success" onClick={() => { props.balance("left") }}>Left</Button>
              </ButtonGroup>
              <ButtonGroup justified bsSize="large">
                <Button bsStyle="primary" onClick={() => { props.balance("center") }}>Center</Button>
              </ButtonGroup>
              <ButtonGroup justified bsSize="large">
                <Button bsStyle="success" onClick={() => { props.balance("right") }}>Right</Button>
              </ButtonGroup>
            </ButtonGroup>
          </Col>
        </FormGroup>
        <FormGroup controlId="bass">
          <Col className="adjustmentLabel"componentClass={ControlLabel} sm={2}>Bass</Col>
          <Col sm={10}>
            <ButtonGroup justified bsSize="large" className="adjustmentGroup">
              <ButtonGroup justified bsSize="large">
                <Button bsStyle="success" onClick={() => { props.bass("down") }}>Down</Button>
              </ButtonGroup>
              <ButtonGroup justified bsSize="large">
                <Button bsStyle="primary" onClick={() => { props.bass("flat") }}>Flat</Button>
              </ButtonGroup>
              <ButtonGroup justified bsSize="large">
                <Button bsStyle="success" onClick={() => { props.bass("up") }}>Up</Button>
              </ButtonGroup>
            </ButtonGroup>
          </Col>
        </FormGroup>
        <FormGroup controlId="treble">
          <Col className="adjustmentLabel"componentClass={ControlLabel} sm={2}>Treble</Col>
          <Col sm={10}>
            <ButtonGroup justified bsSize="large"className="adjustmentGroup">
              <ButtonGroup justified bsSize="large">
                <Button bsStyle="success" onClick={() => { props.treble("down") }}>Down</Button>
              </ButtonGroup>
              <ButtonGroup justified bsSize="large">
                <Button bsStyle="primary" onClick={() => { props.treble("flat") }}>Flat</Button>
              </ButtonGroup>
              <ButtonGroup justified bsSize="large">
                <Button bsStyle="success" onClick={() => { props.treble("up") }}>Up</Button>
              </ButtonGroup>
            </ButtonGroup>
          </Col>
        </FormGroup>
    </Panel>
  )
}

function PanelHeader(props) {
  return (
      <Grid>
        <Row className="show-grid">
          <Col xs={1} className="powerToggle">
            <ToggleButton value={props.power} onToggle={props.onToggle} />
          </Col>
          <Col xs={9}>
            <span className="zoneName">{props.zoneName}</span>
          </Col>
          <Col xs={1}>
            <Button onClick={props.onAdvancedClicked} active={props.advancedVisible}>
              <Glyphicon glyph="cog"/>
            </Button>
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
        <Button bsStyle="success" onClick={() => { props.volumeChanged("down") }}>Volume Down</Button>
      </ButtonGroup>
      <ButtonGroup justified bsSize="large">
        <Button bsStyle="primary" onClick={() => { props.volumeChanged("up") }}>Volume Up</Button>
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
      advancedVisible: false
    };

    this.volumeChanged = this.volumeChanged.bind(this);
    this.sourceChanged = this.sourceChanged.bind(this);
    this.powerToggled = this.powerToggled.bind(this);
    this.bassChanged = this.bassChanged.bind(this);
    this.trebleChanged = this.trebleChanged.bind(this);
    this.balanceChanged = this.balanceChanged.bind(this);
    this.advancedClicked = this.advancedClicked.bind(this);
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

  volumeChanged(type) {
    const url = `${this.props.baseUrl}api/audio/${this.state.zone.zoneId}/volume/${type}`;

    const thing = this;
    fetch(url, {
      method: "POST", mode: 'cors'
    }).then(function () {
      thing.setState({power: true})
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

  bassChanged(type) {
    const url = `${this.props.baseUrl}api/audio/${this.state.zone.zoneId}/bass/${type}`;
    fetch(url, {
      method: "POST", mode: 'cors'
    })
  }

  trebleChanged(type) {
    const url = `${this.props.baseUrl}api/audio/${this.state.zone.zoneId}/treble/${type}`;
    fetch(url, {
      method: "POST", mode: 'cors'
    })
  }

  balanceChanged(type) {
    const url = `${this.props.baseUrl}api/audio/${this.state.zone.zoneId}/balance/${type}`;
    fetch(url, {
      method: "POST", mode: 'cors'
    })
  }

  advancedClicked() {
    this.setState((prevState) => {
      return { advancedVisible: !prevState.advancedVisible }
    })
  }

  render() {
    let panelHeader = (
        <PanelHeader
            zoneName={this.state.zone.name}
            power={this.state.power}
            onToggle={this.powerToggled}
            advancedVisible={this.state.advancedVisible}
            onAdvancedClicked={this.advancedClicked}
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
            volumeChanged={this.volumeChanged}
          />

          <AdvancedPanel
            visible={this.state.advancedVisible}
            bass={this.bassChanged}
            treble={this.trebleChanged}
            balance={this.balanceChanged}
          />
        </Panel>
    )
  };
}

PanelHeader.propTypes = {
  zoneName: PropTypes.string.isRequired,
  power: PropTypes.bool,
  onToggle: PropTypes.func.isRequired,
  advancedVisible: PropTypes.bool.isRequired,
  onAdvancedClicked: PropTypes.func.isRequired
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
  volumeChanged: PropTypes.func.isRequired,
};

AdvancedPanel.propType = {
  visible: PropTypes.bool.isRequired,
  bass: PropTypes.func.isRequired,
  treble: PropTypes.func.isRequired,
  balance: PropTypes.func.isRequired,
};

ZoneInformation.propTypes = {
  zoneInfo: PropTypes.shape({
    zone: PropTypes.shape({
      name: PropTypes.string.isRequired, zoneId: PropTypes.number.isRequired
    }).isRequired, //
    source: PropTypes.shape({
      name: PropTypes.string.isRequired, sourceId: PropTypes.number.isRequired
    }), //
    power: PropTypes.bool
  })
};

export default App;
