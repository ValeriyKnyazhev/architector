import React, { Component } from 'react';
import { BrowserRouter as Router, Redirect, Route, Switch } from 'react-router-dom';
import ProjectsList from './pages/ProjectsList';
import Project from './pages/Project/';
import File from './pages/Project/File';
import Commit from './pages/Project/Commit';
import ProjectChangesHistory from './pages/Project/ProjectChangesHistory';
import ProjectProjection from './pages/Project/ProjectProjection';
import FileChangesHistory from './pages/Project/File/FileChangesHistory';
import FileProjection from './pages/Project/File/FileProjection';
import Navigation from './components/Navigation';
import CodeResolveConflict from './components/CodeResolveConflict';
import './App.css';

class App extends Component {
  render() {
    return (
      <div className="App">
        <Router>
          <div>
            <Route>{({ location }) => location.pathname !== '/register' && <Navigation />}</Route>
            <Switch>
              <Route
                exact
                path="/projects"
                render={({ history }) => <ProjectsList history={history} />}
              />
              <Route exact path="/projects/:projectId" component={Project} />
              <Route exact path="/projects/:projectId/files/:fileId" component={File} />
              <Route exact path="/projects/:projectId/changes" component={ProjectChangesHistory} />
              <Route
                exact
                path="/projects/:projectId/files/:fileId/changes"
                component={FileChangesHistory}
              />
              <Route
                exact
                path="/projects/:projectId/changes/:commitId/content"
                component={ProjectProjection}
              />
              <Route
                exact
                path="/projects/:projectId/files/:fileId/changes/:commitId/content"
                component={FileProjection}
              />
              <Route exact path="/projects/:projectId/changes/:commitId/diff" component={Commit} />
              <Route exact path="/conflict" component={CodeResolveConflict} />
              <Redirect exact from="/" to="/projects" />
            </Switch>
          </div>
        </Router>
      </div>
    );
  }
}

export default App;
