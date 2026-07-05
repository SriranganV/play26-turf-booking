import os
import re

def fix_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    if '"""' not in content:
        return False

    # Regex to find text blocks. We match """ followed by optional whitespace/newline, 
    # then anything non-greedy, until """
    pattern = re.compile(r'"""(.*?)"""', re.DOTALL)

    def replacer(match):
        inner_text = match.group(1)
        lines = inner_text.split('\n')
        
        # Build standard string concatenation
        new_lines = []
        for i, line in enumerate(lines):
            # Escape double quotes
            safe_line = line.replace('"', '\\"')
            
            # First line might be empty if """ was followed immediately by newline
            if i == 0 and not safe_line.strip():
                continue
                
            # Last line might be just indentation
            if i == len(lines) - 1 and not safe_line.strip():
                continue
                
            new_lines.append(f'"{safe_line} "')
            
        return ' +\n'.join(new_lines)

    new_content = pattern.sub(replacer, content)

    with open(filepath, 'w') as f:
        f.write(new_content)
        
    return True

directory = 'src/main/java/turfPlay/turf_booking'

changed_count = 0
for root, dirs, files in os.walk(directory):
    for file in files:
        if file.endswith('.java'):
            filepath = os.path.join(root, file)
            if fix_file(filepath):
                print(f"Fixed {filepath}")
                changed_count += 1

print(f"Total files fixed: {changed_count}")
