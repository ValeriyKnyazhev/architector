import React, { Component } from 'react';
import { BrowserRouter as Router, Route, Switch, Redirect } from 'react-router-dom';
import ProjectsList from './pages/ProjectsList';
import Project from './pages/Project/';
import File from './pages/Project/File';
import './App.css';

class App extends Component {
  render() {
    return (
      <div className="App">
        <Router>
          <Switch>
            <Route
              exact
              path="/projects"
              render={({ history }) => <ProjectsList history={history} />}
            />
            <Route path="/projects/:projectId" component={Project} />
            <Route path="/files/:fileId" component={File} />
            <Redirect exact from="/" to="/projects" />
          </Switch>
        </Router>
      </div>
    );
  }
}

export default App;
