import React, { Component } from "react";
import axios from "axios";
import HistoryChanges from "components/HistoryChanges";
import "./ProjectChangesHistory.sass";

export default class ProjectChangesHistory extends Component {
  state = {
    historyChanges: []
  };

  async componentDidMount() {
    this.fetchProjectHistoryChanges.call(this);
  }

  async fetchProjectHistoryChanges() {
    const {
      match: {
        params: { projectId }
      }
    } = this.props;
    const { data } = await axios.get(`/api/projects/${projectId}/commits`);
    this.setState({ historyChanges: data.commits });
  }

  render() {
    const { historyChanges } = this.state;

    return (
      <div className="container">
        <div>
          <h2>Project changes</h2>
        </div>
        <div>
          <div className="project__changes">
            <HistoryChanges commits={historyChanges} isBriefModel={false}/>
          </div>
        </div>
      </div>
    );
  }
}
