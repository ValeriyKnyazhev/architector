import React, { Component } from "react";
import axios from "axios";
import HistoryChanges from "components/HistoryChanges";
import "./ProjectChangesHistory.sass";

export default class ProjectChangesHistory extends Component {
  state = {
    project: {
      author: "",
      projectName: "",
      description: ""
    },
    historyChanges: []
  };

  async componentDidMount() {
    this.fetchProject.call(this);
    this.fetchProjectHistoryChanges.call(this);
  }

  async fetchProject() {
    const {
      match: {
        params: { projectId }
      }
    } = this.props;
    const { data } = await axios.get(`/api/projects/${projectId}`);
    this.setState({ project: data });
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
    const {
      historyChanges,
      project: { projectName, author, description }
    } = this.state;

    return (
      <div className="container">
        <div>
          <h2>Project changes</h2>
        </div>
        <div className="row project__name">
          <div className="project__name-header col-xs-2 start-xs">Name</div>
          <div className="project__name-info col-xs-10 start-xs">{projectName}</div>
        </div>
        <div className="row project__description">
          <div className="project__description-header col-xs-2 start-xs">Description</div>
          <div className="project__description-info col-xs-10 start-xs">{description}</div>
        </div>
        <div className="project__changes">
          <div className="row project__changes-header">
            <div className="project__changes-header-title col-xs-3 start-xs">Changes</div>
            <div className="col-xs-9 end-xs"/>
          </div>
          <HistoryChanges commits={historyChanges} isBriefModel={false}/>
        </div>
      </div>
    );
  }
}
