import re
with open('core/src/main/java/com/risetobechampion/frontend/combat/CombatantFactory.java', 'r', encoding='utf-8') as f:
    text = f.read()

# Replace ActionState ones
for action in ['ATTACK_BASIC', 'ATTACK_HEAVY', 'SKILL', 'ULTIMATE', 'HIT', 'DEFEATED', 'TAUNT', 'DEFEND']:
    text = text.replace(f'EntityState.{action}', f'ActionState.{action}')
    text = text.replace(f'ActionState.{action}, "', f'"{action}", "')

# Replace Movement ones
for move in ['IDLE', 'WALK', 'JUMP']:
    text = text.replace(f'EntityState.{move}', f'"{move}"')

with open('core/src/main/java/com/risetobechampion/frontend/combat/CombatantFactory.java', 'w', encoding='utf-8') as f:
    f.write(text)
