import pandas as pd
import matplotlib.pyplot as plt
import numpy as np


# Load the CSV file into a DataFrame
df = pd.read_csv('results.csv')


# Create a figure
plt.figure(figsize=(10, 6))

# Convert 'word' to a categorical index for plotting
word_indices = df['word'].to_numpy()

# Ensure that the columns are converted to numpy arrays
automate_time = df['AutomateTime'].to_numpy()
kmp_time = df['KMPTime'].to_numpy()
egrep_time = df['egrepTime'].to_numpy()


# Scatter plot
plt.plot(word_indices, automate_time, label='Method Automate Time (Dots)', marker='o', color='blue')
plt.plot(word_indices, kmp_time, label='Method KMP Time (Dots)', marker='o', color='red')
plt.plot(word_indices, egrep_time, label='Method egrep Time', marker='o', color='green')


# Add labels and title
plt.xlabel('Words')
plt.ylabel('Time (ms)')
plt.title('Comparison of Ahu-Ullman, KMP & egrep Execution Times')
plt.legend()
plt.grid(True)

# Set the x-axis labels to the actual words
plt.xticks(word_indices, df['word'], rotation=90)  # Rotate words on x-axis for better readability

# Ensure the Y-axis is ascending
plt.ylim(bottom=0)  # Optional: You can set the lower limit to zero if needed

plt.tight_layout()  # Adjust layout so that everything fits well

# Show the plot
plt.show()