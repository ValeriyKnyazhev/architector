import React, { Component } from "react";
import axios from "axios";
import HistoryChanges from "components/HistoryChanges";
import "./FileChangesHistory.sass";

export default class FileChangesHistory extends Component {
  state = {
    file: {
      fileName: "",
      schema: ""
    },
    historyChanges: []
  };

  async componentDidMount() {
    this.fetchFileInfo.call(this);
    this.fetchFileHistoryChanges.call(this);
  }

  async fetchFileInfo() {
    const {
      match: {
        params: { projectId, fileId }
      }
    } = this.props;
    const { data } = await axios.get(`/api/projects/${projectId}/files/${fileId}`);
    this.setState({ file: { fileName: data.metadata.name, schema: data.schema } });
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
    const {
      historyChanges,
      file: { fileName, schema }
    } = this.state;

    return (
      <div className="container">
        <div>
          <h2>File changes</h2>
        </div>
        <div className="row file__name">
          <div className="file__name-header col-xs-2 start-xs">Name</div>
          <div className="file__name-info col-xs-10 start-xs">{fileName}</div>
        </div>
        <div className="row file__schema">
          <div className="file__schema-header col-xs-2 start-xs">Schema</div>
          <div className="file__schema-info col-xs-10 start-xs">{schema}</div>
        </div>
        <div className="file__changes">
          <div className="row file__changes-header">
            <div className="file__changes-header-title col-xs-3 start-xs">Changes</div>
            <div className="col-xs-9 end-xs"/>
          </div>
          <HistoryChanges commits={historyChanges} isBriefModel={false}/>
        </div>
      </div>
    );
  }
}
