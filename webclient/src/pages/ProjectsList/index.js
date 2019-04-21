import React, { Component } from "react";
import { Link } from "react-router-dom";
import axios from "axios";
import _isEmpty from "lodash/isEmpty";
import { Button, Icon, Input, message, Modal, Popconfirm } from "antd";
import "./ProjectsList.sass";

export default class Projects extends Component {
  state = {
    projects: [],
    newProjectData: {
      name: "",
      description: ""
    },
    confirmLoading: false,
    visibleCreateProject: false,
    visiblePopup: false
  };

  async componentDidMount() {
    this.fetchProjects.call(this);
  }

  async fetchProjects() {
    const { data: projects } = await axios.get("/api/projects");
    this.setState(projects);
  }

  showModal = state => {
    this.setState({
      [state]: true
    });
  };

  onChangeProjectName = event => {
    this.setState({
      newProjectData: {
        ...this.state.newProjectData,
        name: event.target.value
      }
    });
  };

  onChangeProjectDescription = event => {
    this.setState({
      newProjectData: {
        ...this.state.newProjectData,
        description: event.target.value
      }
    });
  };

  handleCreateProject = modalVisible => {
    const { newProjectData } = this.state;
    this.setState({
      confirmLoading: true
    });
    axios
      .post("/api/projects/", {
        name: newProjectData.name,
        description: newProjectData.description
      })
      .then(() => {
        this.setState(
          {
            [modalVisible]: false,
            confirmLoading: false
          },
          () => {
            this.fetchProjects.call(this);
            message.success("Project was created");
          }
        );
      });
  };

  handlePopup = () => {
    this.setState({ visiblePopup: true });
  };

  handleCancel = state => {
    this.setState({
      [state]: false
    });
  };

  handleClosePopup = () => {
    this.setState({
      visiblePopup: false
    });
  };

  render() {
    const { projects, confirmLoading, visibleCreateProject } = this.state;
    return (
      <div className="container">
        <div>
          <h1>Projects</h1>
        </div>
        <div className="projects__create-project">
          <Button onClick={() => this.showModal("visibleCreateProject")}>
            Create project <Icon type="plus-circle"/>
          </Button>
          {visibleCreateProject && (
            <Modal
              title="Create new project"
              visible={visibleCreateProject}
              onOk={() => this.handleCreateProject("visibleCreateProject")}
              confirmLoading={confirmLoading}
              onCancel={() => this.handleCancel("visibleCreateProject")}
              okButtonProps={{
                disabled: _isEmpty(this.state.newProjectData.name)
              }}
            >
              <div style={{ marginBottom: 16 }}>
                <Input
                  placeholder="Enter your project name"
                  value={this.state.newProjectData.name}
                  onChange={this.onChangeProjectName}
                />
              </div>
              <div style={{ marginBottom: 16 }}>
                <Input
                  placeholder="Enter your project description"
                  value={this.state.newProjectData.description}
                  onChange={this.onChangeProjectDescription}
                />
              </div>
            </Modal>
          )}
          <Popconfirm
            title="Do you want to create new project?"
            visible={this.state.visiblePopup}
            onConfirm={() => this.handleCarded("visibleProjects")}
            onCancel={this.handleClosePopup}
            okText="Yes"
            cancelText="No"
          />
          <br/>
        </div>
        {!_isEmpty(projects) && (
          <div className="row projects__list">
            {projects.map(
              ({
                 projectId,
                 createdDate,
                 updatedDate,
                 projectName,
                 author,
                 files
               }) => (
                <div className="col-xs-12 col-sm-6" key={projectId}>
                  <Link
                    to={{
                      pathname: `/projects/${projectId}`,
                      state: { projectId }
                    }}
                  >
                    <div className="projects__project" key={projectId}>
                      <div className="row projects__project-id">
                        <div className="col-xs-3">Project №</div>
                        <div className="col-xs-9">{projectId}</div>
                      </div>
                      <div className="row projects__project-name">
                        <div className="col-xs-3">Name</div>
                        <div className="col-xs-9">{projectName}
                        </div>
                      </div>
                      <div className="row projects__project-date">
                        <div className="col-xs-3">Created</div>
                        <div className="col-xs-9">{createdDate}</div>
                      </div>
                      <div className="row projects__project-date">
                        <div className="col-xs-3">Updated</div>
                        <div className="col-xs-9">{updatedDate}</div>
                      </div>
                      <div className="row projects__project-author">
                        <div className="col-xs-3">Author</div>
                        <div className="col-xs-9">{author}
                        </div>
                      </div>
                      <div className="row projects__project-files">
                        <div className="col-xs-3">Files</div>
                        <div className="col-xs-9">{files.length}</div>
                      </div>
                    </div>
                  </Link>
                </div>
              )
            )}
          </div>
        )}
      </div>
    );
  }
}
