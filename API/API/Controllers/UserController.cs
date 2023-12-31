using Microsoft.AspNetCore.Mvc;
using FireSharp.Config;
using FireSharp.Interfaces;
using FireSharp.Response;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using API.Models;
using static System.Net.WebRequestMethods;

namespace TimeWise.Controllers
{
    [ApiController]
    [Route("[controller]")]
    public class UserController : ControllerBase
    {
        static IFirebaseConfig config = new FirebaseConfig
        {
            AuthSecret = "AIzaSyAqJvZ0rMbtbDf870COrYrQA--95uDegw8",
            BasePath = "https://nestquest-dbbdd-default-rtdb.firebaseio.com/"
        };
        IFirebaseClient client = new FireSharp.FirebaseClient(config);

        private readonly ILogger<UserController> _logger;

        public UserController(ILogger<UserController> logger)
        {
            _logger = logger;
        }

        [HttpPost("AddUser")]
        public void AddUser([FromBody] User user)
        {
            bool noDups = true;
            FirebaseResponse response = client.Get("users");
            dynamic Userdata = JsonConvert.DeserializeObject<dynamic>(response.Body);
            if (Userdata != null)
            {
                foreach (var item in Userdata)
                {
                    User temp = JsonConvert.DeserializeObject<User>(((JProperty)item).Value.ToString());
                    if (user.UserId == temp.UserId)
                    {
                        noDups = false;
                    }
                }
            }
            if (noDups == true)
            {
                var data = user;
                SetResponse setResponse = client.Set("users/" + data.UserId, data);
                Console.WriteLine("status Code: " + setResponse.StatusCode);
                if (setResponse.StatusCode == System.Net.HttpStatusCode.OK)
                {
                    ModelState.AddModelError(string.Empty, "Added Succesfully");
                }
                else
                {
                    ModelState.AddModelError(string.Empty, "Something went wrong!!");
                }
            }
        }
        [HttpPost("EditUser")]
        public void EditUser(string? UserId, [FromBody] User user)
        {
            if (UserId != null)
            {
                FirebaseResponse FireResponse = client.Get("users/" + UserId);
                User data = JsonConvert.DeserializeObject<User>(FireResponse.Body);
                data.UserId = UserId;
                if (user.Name != null)
                {
                    data.Name = user.Name;
                }
                if (user.Email != null)
                {
                    data.Email = user.Email;
                }
                if (user.darkTheme != null)
                {
                    data.darkTheme = user.darkTheme;
                }
                if (user.maxDistance != null)
                {
                    data.maxDistance = user.maxDistance;
                }
                if (user.metricSystem != null)
                {
                    data.metricSystem = user.metricSystem;
                }
                if (user.birdSightingIds != null)
                {
                    data.birdSightingIds.AddRange(user.birdSightingIds);
                }
                SetResponse response = client.Set("users/" + UserId, data);
            }
        }

        [HttpGet("GetUser")]
        public User GetUser(string? UserId)
        {
            FirebaseResponse response = client.Get("users/" + UserId);
            User data = JsonConvert.DeserializeObject<User>(response.Body);
            return data;
        }
        [HttpGet("GetAllUsers")]
        public List<User> GetAllUsers()
        {
            FirebaseResponse response = client.Get("users");
            dynamic data = JsonConvert.DeserializeObject<dynamic>(response.Body);
            List<User> list = new List<User>();
            if (data != null)
            {
                foreach (var item in data)
                {
                    list.Add(JsonConvert.DeserializeObject<User>(((JProperty)item).Value.ToString()));
                }
            }
            return list;
        }
        [HttpDelete("DeleteUser")]
        public void Delete(string? UserId)
        {
            FirebaseResponse response = client.Delete("users/" + UserId);
        }
    }
}