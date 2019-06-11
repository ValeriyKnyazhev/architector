import React, { Component } from 'react';
import axios from 'axios';
import _isEmpty from 'lodash/isEmpty';
import { Button, Icon, Input, message, Modal, Pagination, Popconfirm, Tag } from 'antd';
import dayjs from 'dayjs';
import './ProjectsList.sass';

function renderAccessRights(accessRights) {
  if (accessRights === 'OWNER') {
    return <Tag color="gold">{accessRights}</Tag>;
  } else if (accessRights === 'WRITE') {
    return <Tag color="blue">{accessRights}</Tag>;
  } else if (accessRights === 'READ') {
    return <Tag color="green">{accessRights}</Tag>;
  } else {
    return <div />;
  }
}

const { TextArea } = Input;
export default class Projects extends Component {
  state = {
    projects: [],
    newProjectData: {
      name: '',
      description: ''
    },
    editedProject: {
      name: '',
      description: ''
    },
    confirmLoading: false,
    visibleCreateProject: false,
    visibleEditProject: false,
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

  onChangeProjectName = (event, data) => {
    this.setState({
      [data]: {
        ...this.state[data],
        name: event.target.value
      }
    });
  };

  onChangeProjectDescription = (event, data) => {
    this.setState({
      [data]: {
        ...this.state[data],
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
      .post('/api/projects', {
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

  handleEditProject = modalVisible => {
    const { editedProject } = this.state;
    this.setState({
      confirmLoading: true
    });
    axios
      .put(`/api/projects/${editedProject.id}`, {
        name: editedProject.name,
        description: editedProject.description
      })
      .then(() => {
        this.setState(
          {
            [modalVisible]: false,
            confirmLoading: false
          },
          () => {
            this.fetchProjects.call(this);
            message.success('Project was edited');
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

  setEditedProject = project => {
    this.setState({
      editedProject: project
    });
  };

  render() {
    const {
      projects,
      confirmLoading,
      visibleCreateProject,
      visibleEditProject,
      editedProject
    } = this.state;

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
                  onChange={e => this.onChangeProjectName(e, 'newProjectData')}
                  label="Name"
                />
              </div>
              <div style={{ marginBottom: 16 }}>
                <div className="projects__input-label">Description</div>
                <TextArea
                  placeholder="Enter your project description"
                  value={this.state.newProjectData.description}
                  onChange={e => this.onChangeProjectDescription(e, 'newProjectData')}
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
        {visibleEditProject && (
          <Modal
            title={`Edit project ${editedProject.name}`}
            visible={visibleEditProject}
            onOk={() => this.handleEditProject('visibleEditProject')}
            confirmLoading={confirmLoading}
            onCancel={() => this.handleCancel('visibleEditProject')}
            okButtonProps={{
              disabled: _isEmpty(editedProject.name)
            }}
          >
            <div style={{ marginBottom: 16 }}>
              <div className="projects__input-label">Name</div>
              <Input
                placeholder="Enter your project name"
                value={editedProject.name}
                onChange={e => this.onChangeProjectName(e, 'editedProject')}
                label="Name"
              />
            </div>
            <div style={{ marginBottom: 16 }}>
              <div className="projects__input-label">Description</div>
              <TextArea
                placeholder="Enter your project description"
                value={editedProject.description}
                onChange={e => this.onChangeProjectDescription(e, 'editedProject')}
              />
            </div>
          </Modal>
        )}
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
                  accessRights,
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
                        <div className="projects__project-field">
                          <div className="projects__field-name">Access Rights:</div>
                          <div className="projects__field-value">
                            {renderAccessRights(accessRights)}
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
                      {(accessRights === 'OWNER' || accessRights === 'WRITE') &&
                      <Button
                        className="projects__project-edit"
                        onClick={e => {
                          e.stopPropagation();
                          this.setEditedProject({ id: projectId, name: projectName, description });
                          this.showModal('visibleEditProject');
                        }}
                      >
                        <Icon type="edit"/>
                      </Button>
                      }
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
