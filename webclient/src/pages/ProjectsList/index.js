import React, { Component } from 'react';
import axios from 'axios';
import _isEmpty from 'lodash/isEmpty';
import { Button, Icon, Input, message, Modal, Popconfirm, Pagination } from 'antd';
import dayjs from 'dayjs';
import './ProjectsList.sass';

const { TextArea } = Input;
export default class Projects extends Component {
  state = {
    projects: [],
    newProjectData: {
      name: '',
      description: ''
    },
    confirmLoading: false,
    visibleCreateProject: false,
    visiblePopup: false,
    currentPage: 1,
    pageSize: 6
  };

  async componentDidMount() {
    this.fetchProjects.call(this);
  }

  async fetchProjects() {
    const { data: projects } = await axios.get('/api/projects');
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
      .post('/api/projects/', {
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

  onChangePage = page => {
    this.setState({
      currentPage: page
    });
  };

  render() {
    const { projects, confirmLoading, visibleCreateProject } = this.state;
    const { history } = this.props;
    return (
      <div className="container">
        <div>
          <h1 className="projects__title">Projects</h1>
        </div>
        <div className="projects__create-project">
          <Button onClick={() => this.showModal('visibleCreateProject')}>
            Create project <Icon type="plus-circle" />
          </Button>
          {visibleCreateProject && (
            <Modal
              title="Create new project"
              visible={visibleCreateProject}
              onOk={() => this.handleCreateProject('visibleCreateProject')}
              confirmLoading={confirmLoading}
              onCancel={() => this.handleCancel('visibleCreateProject')}
              okButtonProps={{
                disabled: _isEmpty(this.state.newProjectData.name)
              }}
            >
              <div style={{ marginBottom: 16 }}>
                <div className="projects__input-label">Name</div>
                <Input
                  placeholder="Enter your project name"
                  value={this.state.newProjectData.name}
                  onChange={this.onChangeProjectName}
                  label="Name"
                />
              </div>
              <div style={{ marginBottom: 16 }}>
                <div className="projects__input-label">Description</div>
                <TextArea
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
            onConfirm={() => this.handleCarded('visibleProjects')}
            onCancel={this.handleClosePopup}
            okText="Yes"
            cancelText="No"
          />
          <br />
        </div>
        {!_isEmpty(projects) ? (
          <div className="row projects__list">
            {projects
              .slice(
                this.state.pageSize * (this.state.currentPage - 1),
                this.state.pageSize * this.state.currentPage
              )
              .map(
                ({
                  projectId,
                  createdDate,
                  updatedDate,
                  projectName,
                  author,
                  files,
                  description
                }) => (
                  <div className="col-xs-12 col-sm-6" key={projectId}>
                    <div
                      className="projects__project"
                      onClick={() => history.push(`/projects/${projectId}`)}
                    >
                      <div className="row projects__project-name">
                        <div className="col-xs-9">{projectName}</div>
                      </div>
                      <div className="row projects__project-descr">
                        <div className="col-xs-9">{description}</div>
                      </div>
                      <div className="projects__fields">
                        <div className="projects__project-field">
                          <div className="projects__field-name">Created:</div>
                          <div className="projects__field-value">
                            {dayjs(createdDate).format('YYYY-MM-DD')}
                          </div>
                        </div>
                        <div className="projects__project-field">
                          <div className="projects__field-name">Updated:</div>
                          <div className="projects__field-value">
                            {dayjs(updatedDate).format('YYYY-MM-DD')}
                          </div>
                        </div>
                      </div>
                      <div className="projects__fields">
                        <div className="projects__project-field">
                          <div className="projects__field-name">Author:</div>
                          <div className="projects__field-value">{author}</div>
                        </div>
                      </div>
                      <div className="row projects__project-files">
                        <div className="col-xs-3">Files</div>
                        <div className="col-xs-9">{files.length}</div>
                      </div>
                    </div>
                  </div>
                )
              )}
          </div>
        ) : (
          <p className="projects__not-created">Projects not created</p>
        )}
        {projects.length > 0 && (
          <Pagination
            current={this.state.currentPage}
            pageSize={this.state.pageSize}
            onChange={this.onChangePage}
            total={projects.length}
          />
        )}
      </div>
    );
  }
}
