# Git Configuration Troubleshooting

If you encounter an error like `fatal: bad boolean config value 'flase' for 'http.sslverify'`, it means there is a typo in your Git configuration. You can fix it with the following steps:

1. Correct the configuration key:
   ```bash
   git config --global --unset http.sslVerify
   git config --global http.sslVerify false
   ```
2. Alternatively, open the Git configuration file directly:
   - Global config: `~/.gitconfig`
   - Project config: `.git/config`

   Remove or fix the incorrect entry (`flase` -> `false`).

3. After fixing the configuration, rerun your Git command, for example:
   ```bash
   git fetch
   git pull
   ```

This should resolve the issue and allow you to fetch or pull updates without errors.
