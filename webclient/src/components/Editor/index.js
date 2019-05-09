import React from "react";
import { ContentState, Editor, EditorState } from "draft-js";
import "./Editor.css";

export default function CodeEditor({ content }) {
  const [editorState, setEditorState] = React.useState(
    EditorState.createWithContent(ContentState.createFromText(content))
  );

  const editor = React.useRef(null);

  function focusEditor() {
    editor.current.focus();
  }

  React.useEffect(() => {
    focusEditor();
  }, []);

  return (
    <div onClick={focusEditor}>
      <Editor
        ref={editor}
        editorState={editorState}
        blockStyleFn={() => "leftStyle"}
        onChange={editorState => setEditorState(editorState)}
        readOnly={true}
      />
    </div>
  );
}
