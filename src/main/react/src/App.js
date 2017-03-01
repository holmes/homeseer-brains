import React, {Component, PropTypes} from 'react';
import './App.css';

import {
  Button,
  DropdownButton,
  ButtonGroup,
  MenuItem,
  Panel,
  Grid,
  Row,
  Col,
  FormGroup,
  Form,
  ControlLabel,
  Glyphicon
} from 'react-bootstrap';
import ToggleButton from 'react-toggle-button';
import Loadable from 'react-loading-overlay';

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
      loaded: false, baseUrl: remoteHost
    };
  }

  componentDidMount() {
    let thing = this;

    fetch(`${this.state.baseUrl}api/audio/zoneInfo`, {mode: 'cors'})
        .then(function (response) {
          return response.json()
        }).then(function (json) {
      thing.setState({
        loaded: true, sources: json.sources, zoneInformation: json.zoneInformation
      })
    }).catch(function (ex) {
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
        <Form horizontal>

          <FormGroup controlId="formHorizontalEmail">
            <Col componentClass={ControlLabel} sm={2}>
              Loudness
            </Col>
            <Col sm={10}>
              <ToggleButton value={props.loudness} onToggle={props.loudnessToggled}/>
            </Col>
          </FormGroup>

          <FormGroup controlId="balance">
            <Col className="adjustmentLabel" componentClass={ControlLabel} sm={2}>Balance</Col>
            <Col sm={10}>
              <ButtonGroup justified bsSize="large" className="adjustmentGroup">
                <ButtonGroup justified bsSize="large">
                  <Button bsStyle="success" onClick={() => {
                    props.balanceChanged("left")
                  }}>Left</Button>
                </ButtonGroup>
                <ButtonGroup justified bsSize="large">
                  <Button disabled>{props.balance}</Button>
                </ButtonGroup>
                <ButtonGroup justified bsSize="large">
                  <Button bsStyle="success" onClick={() => {
                    props.balanceChanged("right")
                  }}>Right</Button>
                </ButtonGroup>
              </ButtonGroup>
            </Col>
          </FormGroup>

          <FormGroup controlId="bass">
            <Col className="adjustmentLabel" componentClass={ControlLabel} sm={2}>Bass</Col>
            <Col sm={10}>
              <ButtonGroup justified bsSize="large" className="adjustmentGroup">
                <ButtonGroup justified bsSize="large">
                  <Button bsStyle="success" onClick={() => {
                    props.bassChanged("down")
                  }}>Down</Button>
                </ButtonGroup>
                <ButtonGroup justified bsSize="large">
                  <Button disabled>{props.bass}</Button>
                </ButtonGroup>
                <ButtonGroup justified bsSize="large">
                  <Button bsStyle="success" onClick={() => {
                    props.bassChanged("up")
                  }}>Up</Button>
                </ButtonGroup>
              </ButtonGroup>
            </Col>
          </FormGroup>

          <FormGroup controlId="treble">
            <Col className="adjustmentLabel" componentClass={ControlLabel} sm={2}>Treble</Col>
            <Col sm={10}>
              <ButtonGroup justified bsSize="large" className="adjustmentGroup">
                <ButtonGroup justified bsSize="large">
                  <Button bsStyle="success" onClick={() => {
                    props.trebleChanged("down")
                  }}>Down</Button>
                </ButtonGroup>
                <ButtonGroup justified bsSize="large">
                  <Button disabled>{props.treble}</Button>
                </ButtonGroup>
                <ButtonGroup justified bsSize="large">
                  <Button bsStyle="success" onClick={() => {
                    props.trebleChanged("up")
                  }}>Up</Button>
                </ButtonGroup>
              </ButtonGroup>
            </Col>
          </FormGroup>
        </Form>
      </Panel>
  )
}

function PanelHeader(props) {
  return (
      <Grid>
        <Row className="show-grid">
          <Col xs={2} className="powerToggle">
            <ToggleButton value={props.power} onToggle={props.onToggle}/>
          </Col>
          <Col xs={8}>
            <span className="zoneName">{props.zoneName}</span>
          </Col>
          <Col xs={2}>
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
          <Button bsStyle="success" onClick={() => {
            props.volumeChanged("down")
          }}><Glyphicon glyph="volume-down"/></Button>
        </ButtonGroup>
        <ButtonGroup justified bsSize="large">
          <Button disabled>{props.volume}</Button>
        </ButtonGroup>
        <ButtonGroup justified bsSize="large">
          <Button bsStyle="primary" onClick={() => {
            props.volumeChanged("up")
          }}><Glyphicon glyph="volume-up"/></Button>
        </ButtonGroup>
      </ButtonGroup>
  )
}

class ZoneInformation extends React.Component {
  constructor(props) {
    super(props);

    let zone = props.zoneInfo.zone;
    let sourceId = props.zoneInfo.source && props.zoneInfo.source.sourceId;

    this.state = {
      zone: zone,
      sourceId: sourceId,
      power: props.zoneInfo.power,
      volume: props.zoneInfo.volume,
      loudness: props.zoneInfo.loudness,
      balance: props.zoneInfo.balance,
      bass: props.zoneInfo.bass,
      treble: props.zoneInfo.treble,
      advancedVisible: false,
      requestInFlight: false
    };

    this.volumeChanged = this.volumeChanged.bind(this);
    this.sourceChanged = this.sourceChanged.bind(this);
    this.powerToggled = this.powerToggled.bind(this);
    this.bassChanged = this.bassChanged.bind(this);
    this.trebleChanged = this.trebleChanged.bind(this);
    this.balanceChanged = this.balanceChanged.bind(this);
    this.loudnessToggled = this.loudnessToggled.bind(this);
    this.advancedClicked = this.advancedClicked.bind(this);
  }

  sourceChanged(sourceId) {
    const url = `${this.props.baseUrl}api/audio/${this.state.zone.zoneId}/source/${sourceId}`;
    this.postRequest(url)
  }

  volumeChanged(type) {
    const url = `${this.props.baseUrl}api/audio/${this.state.zone.zoneId}/volume/${type}`;
    this.postRequest(url)
  }

  powerToggled(originalPower) {
    const power = !originalPower;
    const url = `${this.props.baseUrl}api/audio/${this.state.zone.zoneId}/power/${power}`;
    this.postRequest(url)
  }

  loudnessToggled(originalLoudness) {
    const loudness = !originalLoudness;
    const url = `${this.props.baseUrl}api/audio/${this.state.zone.zoneId}/loudness/${loudness}`;
    this.postRequest(url)
  }

  bassChanged(type) {
    const url = `${this.props.baseUrl}api/audio/${this.state.zone.zoneId}/bass/${type}`;
    this.postRequest(url)

  }

  trebleChanged(type) {
    const url = `${this.props.baseUrl}api/audio/${this.state.zone.zoneId}/treble/${type}`;
    this.postRequest(url)

  }

  balanceChanged(type) {
    const url = `${this.props.baseUrl}api/audio/${this.state.zone.zoneId}/balance/${type}`;
    this.postRequest(url)
  }

  advancedClicked() {
    this.setState((prevState) => {
      return {advancedVisible: !prevState.advancedVisible}
    })
  }

  postRequest(url) {
    this.setState({
      requestInFlight: true
    });

    const thing = this;
    fetch(url, {
      method: "POST", mode: 'cors'
    }).then(function (response) {
      return response.json()
    }).then(function (json) {
      thing.updateState(json);
    }).catch(function() {
      thing.setState({
        requestInFlight: false
      });
    });
  }

  updateState(zoneInfo) {
    this.setState({
      requestInFlight: false,
      zone: zoneInfo.zone,
      sourceId: zoneInfo.source.sourceId,
      power: zoneInfo.power,
      volume: zoneInfo.volume,
      loudness: zoneInfo.loudness,
      balance: zoneInfo.balance,
      bass: zoneInfo.bass,
      treble: zoneInfo.treble,
    });
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
        <Loadable animate={false} active={this.state.requestInFlight}>
          <Panel header={panelHeader}>
            <SourceSelector
                zoneId={this.state.zone.zoneId}
                selectedSourceId={this.state.sourceId}
                sources={this.props.sources}
                sourceChanged={this.sourceChanged}
            />

            <VolumeSection
                volume={this.state.volume}
                volumeChanged={this.volumeChanged}
            />

            <AdvancedPanel
                visible={this.state.advancedVisible}
                loudness={this.state.loudness}
                balance={this.state.balance}
                bass={this.state.bass}
                treble={this.state.treble}
                loudnessToggled={this.loudnessToggled}
                balanceChanged={this.balanceChanged}
                bassChanged={this.bassChanged}
                trebleChanged={this.trebleChanged}
            />
          </Panel>
        </Loadable>
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
  })).isRequired, sourceChanged: PropTypes.func.isRequired
};

VolumeSection.propTypes = {
  volume: PropTypes.number.isRequired,
  volumeChanged: PropTypes.func.isRequired,
};

AdvancedPanel.propType = {
  visible: PropTypes.bool.isRequired,
  loudness: PropTypes.bool.isRequired,
  balance: PropTypes.number.isRequired,
  bass: PropTypes.number.isRequired,
  treble: PropTypes.number.isRequired,
  bassChanged: PropTypes.func.isRequired,
  trebleChanged: PropTypes.func.isRequired,
  balanceChanged: PropTypes.func.isRequired,
  loudnessToggled: PropTypes.func.isRequired
};

ZoneInformation.propTypes = {
  zoneInfo: PropTypes.shape({
    zone: PropTypes.shape({
      name: PropTypes.string.isRequired, zoneId: PropTypes.number.isRequired
    }).isRequired, //
    source: PropTypes.shape({
      name: PropTypes.string.isRequired, sourceId: PropTypes.number.isRequired
    }), //
    power: PropTypes.bool, loudness: PropTypes.bool,
  })
};

export default App;
