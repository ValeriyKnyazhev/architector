import React, { Component } from "react";
import { BrowserRouter as Router, Redirect, Route, Switch } from "react-router-dom";
import ProjectsList from "./pages/ProjectsList";
import Project from "./pages/Project/";
import File from "./pages/Project/File";
import ProjectChangesHistory from "./pages/Project/ProjectChangesHistory";
import FileChangesHistory from "./pages/Project/File/FileChangesHistory";
import "./App.css";

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
            <Route exact path="/projects/:projectId" component={Project}/>
            <Route exact path="/projects/:projectId/files/:fileId" component={File}/>
            <Route exact path="/projects/:projectId/changes" component={ProjectChangesHistory}/>
            <Route
              exact
              path="/projects/:projectId/files/:fileId/changes"
              component={FileChangesHistory}
            />
            <Redirect exact from="/" to="/projects" />
          </Switch>
        </Router>
      </div>
    );
  }
}

export default App;
