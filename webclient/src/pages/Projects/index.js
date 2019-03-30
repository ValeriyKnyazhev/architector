import React, { Component } from 'react';
import { Link } from 'react-router-dom';
import axios from 'axios';
import _isEmpty from 'lodash/isEmpty';
import { Button, Icon, Input, Modal, Popconfirm, message } from 'antd';
import './Projects.sass';

function constructSourceUrl(value) {
    return value.startsWith("https://") || value.startsWith("http://")
        ? value
        : "https://" + value
  };

export default class Projects extends Component {
  state = {
    projects: [],
    newProjectSourceUrl: '',
    confirmLoading: false,
    visibleCreateProject: false,
    visiblePopup: false
  };

  async componentDidMount() {
    this.fetchProjects.call(this);
  };

  async fetchProjects() {
    const { data: projects } = await axios.get('/api/projects');
    this.setState(projects);
  };

  showModal = state => {
    this.setState({
      [state]: true
    });
  };

  onChangeSourceUrl = event => {
    this.setState({ newProjectSourceUrl: event.target.value});
  };

  handleCreateProjectFromSource = modalVisible => {
    const { newProjectSourceUrl } = this.state;
    this.setState({
      confirmLoading: true
    });
    axios
      .post('/api/projects/source', {
        sourceUrl: constructSourceUrl(newProjectSourceUrl)
      })
      .then(() => {
        this.setState(
          {
            [modalVisible]: false,
            confirmLoading: false
          },
          () => {
            this.fetchProjects.call(this);
            message.success('Project was created');
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

  selectRow = rows => {
    this.setState({ projects: rows });
  };

  render() {
    const {
      projects,
      confirmLoading,
      visibleCreateProject
    } = this.state;
    return (
      <div className="container">
        <div>
          <h1>Projects</h1>
        </div>
        <section>
          <div className="projects__create-project">
            <Button onClick={() => this.showModal('visibleCreateProject')}>
              Create project <Icon type="plus-circle" />
            </Button>
            {visibleCreateProject && (
              <Modal
                title="Create new project"
                visible={visibleCreateProject}
                onOk={() => this.handleCreateProjectFromSource('visibleCreateProject')}
                confirmLoading={confirmLoading}
                onCancel={() => this.handleCancel('visibleCreateProject')}
                okButtonProps={{
                  disabled: _isEmpty(this.state.newProjectSourceUrl)
                }}
              >
                <div style={{ marginBottom: 16 }}>
                    <Input
                        placeholder="Enter your source URL"
                        value={this.state.newProjectSourceUrl}
                        onChange={this.onChangeSourceUrl}
                        addonBefore="Https://"
                    />
                </div>
              </Modal>
            )}
            <Popconfirm
              title="Do you want to create new project?"
              visible={this.state.visiblePopup}
              onConfirm={() => this.handleCarded('visibleProjects')}
              onCancel={this.handleClosePopup}
              okText="Yes"
              cancelText="No"
            />
            <br />
          </div>
          {!_isEmpty(projects) && (
            <div className="row projects__list">
              {projects.map(
                ({ projectId, createdDate, updatedDate, schema, files, metadata }) => (
                  <div className="col-xs-12 col-sm-6" key={projectId}>
                    <Link
                      to={{ pathname: `/api/projects/${projectId}`, state: { files } }}
                    >
                      <div className="projects__project" key={projectId}>
                        <div className="row projects__project-id">
                          <div className="col-xs-3">Project № </div>
                          <div className="col-xs-9">{projectId}</div>
                        </div>
                        <div className="row projects__project-date">
                          <div className="col-xs-3">Created </div>
                          <div className="col-xs-9">{createdDate}</div>
                        </div>
                        <div className="row projects__project-date">
                          <div className="col-xs-3">Updated </div>
                          <div className="col-xs-9">{updatedDate}</div>
                        </div>
                        <div className="row projects__project-schema">
                          <div className="col-xs-3">Schema </div>
                          <div className="col-xs-9">{schema}</div>
                        </div>
                        <div className="projects__project-metadata">
                          <div className="row projects__project-metadata-name">
                            <div className="col-xs-3">Name </div>
                            <div className="col-xs-9">{metadata.name}</div>
                          </div>
                          <div className="row row projects__project-metadata-authors">
                           <div className="col-xs-3">Authors </div>
                           <div className="col-xs-9">{metadata.authors.filter(a => a).length > 0
                             ? metadata.authors.reduce((a1, a2) => a1 + ", " + a2)
                             : 'N/A'}
                           </div>
                          </div>
                        </div>
                        <div className="row projects__project-files">
                          <div className="col-xs-3">Files </div>
                          <div className="col-xs-9">{files.length}</div>
                        </div>
                      </div>
                    </Link>
                  </div>
                )
              )}
            </div>
          )}
        </section>
      </div>
    );
  }
}