# Branch protection for master

To prevent direct pushes to the protected branch:

1. Go to GitHub repository settings.
2. Open "Branches".
3. Add a branch protection rule for `master`.
4. Enable:
   - "Require a pull request before merging"
   - "Require status checks to pass before merging"
   - Select the checks from the PR workflow, including:
     - `Frontend tests and build`
     - `Backend tests and build`
5. Optionally enable:
   - "Require branches to be up to date before merging"
   - "Restrict who can push to matching branches"

This prevents direct pushes and ensures every change to `master` passes CI first.
