import React, { Component } from "react";
import axios from "axios";
import HistoryChanges from "components/HistoryChanges";
import "./FileChangesHistory.sass";

export default class FileChangesHistory extends Component {
  state = {
    historyChanges: []
  };

  async componentDidMount() {
    this.fetchFileHistoryChanges.call(this);
  }

  async fetchFileHistoryChanges() {
    const {
      match: {
        params: { projectId, fileId }
      }
    } = this.props;
    const { data } = await axios.get(`/api/projects/${projectId}/files/${fileId}/commits`);
    this.setState({ historyChanges: data.commits });
  }

  render() {
    const { historyChanges } = this.state;

    return (
      <div className="container">
        <div>
          <h2>File changes</h2>
        </div>
        <div>
          <div className="file__changes">
            <HistoryChanges commits={historyChanges} isBriefModel={false}/>
          </div>
        </div>
      </div>
    );
  }
}
