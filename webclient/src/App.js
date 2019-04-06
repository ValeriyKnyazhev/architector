import React, { Component } from "react";
import {
  BrowserRouter as Router,
  Route,
  Switch,
  Redirect
} from "react-router-dom";
import ProjectsList from './pages/ProjectsList';
import Project from './pages/Project/';
import "./App.css";

const Files = () => <div> Files </div>;

class App extends Component {
  render() {
    return <div className="App">
        <Router>
            <Switch>
                <Route exact path="/projects" component={ProjectsList} />
                <Route path="/projects/:projectId" component={Project} />
                <Route path="/projects/:projectId/files" component={Files} />
                <Redirect exact from="/" to="/projects" />
            </Switch>
        </Router>
    </div>;
  }
}

export default App;
