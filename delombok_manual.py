import os
import re

def process_file(filepath):
    with open(filepath, 'r') as f:
        lines = f.readlines()

    # check if file uses lombok
    if not any('lombok' in line for line in lines):
        return

    # remove lombok imports and annotations
    new_lines = []
    class_name = None
    fields = []
    
    in_class = False
    
    for line in lines:
        if 'import lombok' in line:
            continue
        
        # remove annotations
        if line.strip() in ['@Data', '@Getter', '@Setter', '@NoArgsConstructor', '@AllArgsConstructor', '@Builder']:
            continue
        
        # match class declaration
        class_match = re.search(r'public\s+class\s+(\w+)', line)
        if class_match:
            class_name = class_match.group(1)
            in_class = True
            new_lines.append(line)
            continue
            
        if in_class:
            # match field declarations
            # e.g. private String name;
            # e.g. private Long id = 0L;
            # e.g. @Column(...) private String name;
            field_match = re.search(r'private\s+([\w<>]+)\s+(\w+)\s*(?:=.*)?;', line)
            if field_match:
                type_name = field_match.group(1)
                field_name = field_match.group(2)
                fields.append((type_name, field_name))
        
        new_lines.append(line)

    if not class_name or not fields:
        # just write the file without lombok annotations
        with open(filepath, 'w') as f:
            f.writelines(new_lines)
        return

    # generate code
    code = []
    
    # default constructor
    code.append(f"    public {class_name}() {{}}\n\n")
    
    # all args constructor
    args = ", ".join([f"{t} {n}" for t, n in fields])
    code.append(f"    public {class_name}({args}) {{\n")
    for t, n in fields:
        code.append(f"        this.{n} = {n};\n")
    code.append("    }\n\n")
    
    # getters and setters
    for type_name, field_name in fields:
        cap_name = field_name[0].upper() + field_name[1:]
        
        # getter
        code.append(f"    public {type_name} get{cap_name}() {{\n")
        code.append(f"        return {field_name};\n")
        code.append("    }\n\n")
        
        # setter
        code.append(f"    public void set{cap_name}({type_name} {field_name}) {{\n")
        code.append(f"        this.{field_name} = {field_name};\n")
        code.append("    }\n\n")

    # builder pattern
    code.append(f"    public static {class_name}Builder builder() {{\n")
    code.append(f"        return new {class_name}Builder();\n")
    code.append("    }\n\n")
    
    code.append(f"    public static class {class_name}Builder {{\n")
    for t, n in fields:
        code.append(f"        private {t} {n};\n")
    for t, n in fields:
        cap_name = n[0].upper() + n[1:]
        code.append(f"        public {class_name}Builder {n}({t} {n}) {{\n")
        code.append(f"            this.{n} = {n};\n")
        code.append("            return this;\n")
        code.append("        }\n")
    code.append(f"        public {class_name} build() {{\n")
    code.append(f"            return new {class_name}({', '.join([n for t, n in fields])});\n")
    code.append("        }\n")
    code.append("    }\n")

    # insert code before the last closing brace
    for i in range(len(new_lines) - 1, -1, -1):
        if '}' in new_lines[i]:
            # insert before this line
            # we need to be careful if there are methods, but usually DTOs just end with }
            # Let's insert exactly before the last }
            new_lines.insert(i, "".join(code))
            break
            
    with open(filepath, 'w') as f:
        f.writelines(new_lines)
    print(f"Processed {filepath}")

# process all java files
for root, dirs, files in os.walk('c:/Users/laptop/Desktop/PROJECTS/PaymentWallet'):
    for file in files:
        if file.endswith('.java'):
            process_file(os.path.join(root, file))

