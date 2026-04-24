# Changelog — Rename "Facilities Manager" → "Organization Admin"

Date: 2026-04-24
Author: automation (assistant)

Summary
-------
Controlled, atomic replacement of the mentions and identifiers "Facilities Manager" (and variants) with "Organization Admin" / `ORGANIZATION_ADMIN` in documentation and planning artifacts. This change aligns terminology in the PRD (SaaS demo mode) with implementation artifacts.

Modified files
--------------
- `_bmad-output/implementation-artifacts/sprint-status.yaml`
  - `1-4-facilities-manager-user-administration` → `1-4-organization-admin-user-administration`
  - `4-3-facilities-manager-incident-overview` → `4-3-organization-admin-incident-overview`

- `_bmad-output/planning-artifacts/prd.md`
  - Updated note indicating legacy occurrences have been replaced (SaaS demo context).

- `_bmad-output/planning-artifacts/ux-design-specification.md`
  - Inline textual mentions updated: "Facilities Manager" → "Organization Admin" (persona, descriptions, chosen direction).

- `_bmad-output/planning-artifacts/ux-design-directions.html`
  - Admin badge/label updated.

- `_bmad-output/planning-artifacts/epics.md`
  - Mentions and FR/epic labels updated for `Organization Admin`.

- `_bmad-output/planning-artifacts/architecture.md`
  - RBAC table: `FACILITIES_MANAGER` → `ORGANIZATION_ADMIN`.

- `project/roadmap.md`
  - Story titles and roadmap lines updated to `Organization Admin`.

- `README.md`
  - All visible occurrences replaced with `Organization Admin`.

Why / Impact
-------------
- Terminology now consistent with the PRD decision (SaaS demo mode).
- Replacements were applied atomically and documented to minimize inconsistency risk.
- Residual risk: if external scripts or automation depend on the old story identifiers (exact story names), those references must be updated. A global search was performed — only the files listed above contained legacy mentions.

Recommended Git actions
-----------------------
1. Create a working branch:

```bash
git checkout -b feat/rename-facilities-manager-to-organization-admin
```

2. Stage and commit the changes:

```bash
git add CHANGELOG_RENAMES.md \
    _bmad-output/implementation-artifacts/sprint-status.yaml \
    _bmad-output/planning-artifacts/prd.md \
    _bmad-output/planning-artifacts/ux-design-specification.md \
    _bmad-output/planning-artifacts/ux-design-directions.html \
    _bmad-output/planning-artifacts/epics.md \
    _bmad-output/planning-artifacts/architecture.md \
    project/roadmap.md \
    README.md
git commit -m "refactor(docs): rename 'Facilities Manager' → 'Organization Admin' across planning artifacts"
git push origin feat/rename-facilities-manager-to-organization-admin
```

3. Open a Pull Request (recommended title):

```
refactor(docs): rename 'Facilities Manager' → 'Organization Admin'
```

PR checklist
------------
- [ ] Verify no CI scripts or external tools depend on old story/ID names.
- [ ] Proofread textual changes for tone and formatting consistency (EN preferred).
- [ ] Link the PR to any tracking ticket if relevant.

Technical notes
---------------
- Story identifiers in `sprint-status.yaml` were renamed — if any automation generates or consumes these keys, update those integrations accordingly.
- No executable code (backend/frontend) was modified — only planning and documentation artifacts.

If you want, I can:
- prepare the commit and open the PR automatically (if the remote is reachable from the environment), or
- produce a unified diff for review here.

*** End of changelog
