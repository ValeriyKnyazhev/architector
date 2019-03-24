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
                  <div className="col-xs-12 col-sm-4" key={projectId}>
                    <Link
                      to={{ pathname: `/api/projects/${projectId}`, state: { files } }}
                    >
                      <div className="projects__invoice" key={projectId}>
                        <div className="projects__project-id">
                          Project № {projectId}
                        </div>
                        <div className="projects__project-date">
                          Created: {createdDate}
                        </div>
                        <div className="projects__project-date">
                          Updated: {updatedDate}
                        </div>
                        <div className="projects__project-schema">
                          Schema: {schema}
                        </div>
                        <div className="projects__project-metadata">
                            <div className="projects__project-metadata-name">
                                Name: {metadata.name}
                            </div>
                            <div className="projects__project-metadata-authors">
                                Authors: {metadata.authors.reduce((a1, a2) => a1 + "," + a2)}
                            </div>
                        </div>
                        <div className="projects__project-files">
                          Files: {files.length}
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