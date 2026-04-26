import os
import re

def process_file(filepath):
    with open(filepath, 'r') as f:
        lines = f.readlines()

    if not any('@RequiredArgsConstructor' in line for line in lines):
        return

    new_lines = []
    class_name = None
    final_fields = []
    
    in_class = False
    has_required_args = False
    
    for line in lines:
        if 'import lombok' in line:
            continue
            
        if '@RequiredArgsConstructor' in line:
            has_required_args = True
            continue
            
        class_match = re.search(r'public\s+class\s+(\w+)', line)
        if class_match:
            class_name = class_match.group(1)
            in_class = True
            new_lines.append(line)
            continue
            
        if in_class and has_required_args:
            # match private final fields
            field_match = re.search(r'private\s+final\s+([\w<>]+)\s+(\w+)\s*;', line)
            if field_match:
                type_name = field_match.group(1)
                field_name = field_match.group(2)
                final_fields.append((type_name, field_name))
                
        new_lines.append(line)

    if class_name and has_required_args and final_fields:
        code = []
        args = ", ".join([f"{t} {n}" for t, n in final_fields])
        code.append(f"\n    public {class_name}({args}) {{\n")
        for t, n in final_fields:
            code.append(f"        this.{n} = {n};\n")
        code.append("    }\n")
        
        # insert constructor after the last final field
        # find index of last final field
        last_idx = 0
        for i, line in enumerate(new_lines):
            if 'private final' in line:
                last_idx = i
        
        new_lines.insert(last_idx + 1, "".join(code))
        
    with open(filepath, 'w') as f:
        f.writelines(new_lines)
    print(f"Processed constructors for {filepath}")

for root, dirs, files in os.walk('c:/Users/laptop/Desktop/PROJECTS/PaymentWallet'):
    for file in files:
        if file.endswith('.java'):
            process_file(os.path.join(root, file))

