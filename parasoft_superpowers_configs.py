import os

# =============================================================================
# jira-to-spec skill
# =============================================================================

# Required by jira-to-spec: Jira server base URL (without trailing slash)
JIRA_URL = "https://jira.parasoft.com"

# Required by jira-to-spec: Access token used by Jira API requests.
# Recommended: set environment variable JIRA_TOKEN and replace the value below with:
# os.getenv("JIRA_TOKEN")
JIRA_TOKEN = os.getenv("JIRA_TOKEN", "")

# Optional (jira-to-spec): Directory for spec files, relative to workspace root.
# If omitted or set to "", spec auto-save is disabled and spec content is shown in chat only.
SPEC_OUTPUT_DIR = "specs"
