namespace API
{
    public class User
    {
        public string? UserId { get; set; }
        public string? Name { get; set; }
        public string? Email { get; set; }
        public List<string>? birdSightingIds { get; set; }
    }
}