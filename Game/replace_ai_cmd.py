import os

cmd_dir = 'core/src/main/java/com/risetobechampion/frontend/command'
for file in os.listdir(cmd_dir):
    if file.endswith('.java'):
        path = os.path.join(cmd_dir, file)
        with open(path, 'r', encoding='utf-8') as f:
            text = f.read()
        
        text = text.replace('import com.risetobechampion.frontend.combat.EntityState;', 'import com.risetobechampion.frontend.combat.ActionState;')
        text = text.replace('EntityState.', 'ActionState.')
        
        with open(path, 'w', encoding='utf-8') as f:
            f.write(text)

ai_path = 'core/src/main/java/com/risetobechampion/frontend/combat/AggressiveAi.java'
with open(ai_path, 'r', encoding='utf-8') as f:
    text = f.read()
text = text.replace('import com.risetobechampion.frontend.combat.EntityState;', 'import com.risetobechampion.frontend.combat.ActionState;\nimport com.risetobechampion.frontend.combat.MovementState;')
text = text.replace('EntityState.', 'ActionState.')
text = text.replace('self.setState(', 'self.setActionState(')
with open(ai_path, 'w', encoding='utf-8') as f:
    f.write(text)
